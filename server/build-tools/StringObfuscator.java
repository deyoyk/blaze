import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Encrypts string constants in Blaze classes after ProGuard has finished.
 * Only classes below lol/deyo/blaze are touched; Bukkit, CraftBukkit, NMS
 * and shaded libraries keep their original strings.
 */
public final class StringObfuscator {
    private static final String TARGET_PREFIX = "lol/deyo/blaze/";
    private static final String DECRYPTOR_CLASS =
            "lol/deyo/blaze/owdcgqtidhnvxutcxbwpyqusqvwfoomcvtogtquwoepzeypynx";
    private static final String DECRYPTOR_METHOD =
            "uatrfyckmltoupukaegmjhgzaubuosgyrqtmjdnpdidjcakaet";
    private static final int BASE_KEY = 0x3C;
    private static final int MULTIPLIER = 31;
    private static final int ASM_API = Opcodes.ASM7;

    private StringObfuscator() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: StringObfuscator <jar>");
        }

        Path jar = Paths.get(args[0]);
        Path temporary = Paths.get(jar.toString() + ".strings.tmp");
        int classCount = 0;
        int stringCount = 0;

        try (ZipFile input = new ZipFile(jar.toFile());
             ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(temporary))) {
            Enumeration<? extends ZipEntry> entries = input.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    ZipEntry directory = new ZipEntry(entry.getName());
                    directory.setTime(entry.getTime());
                    output.putNextEntry(directory);
                    output.closeEntry();
                    continue;
                }
                if (entry.getName().equals(DECRYPTOR_CLASS + ".class")) {
                    throw new IllegalStateException("String decryptor class already exists: " + DECRYPTOR_CLASS);
                }

                byte[] data;
                try (InputStream stream = input.getInputStream(entry)) {
                    data = readFully(stream);
                }

                if (entry.getName().startsWith(TARGET_PREFIX) && entry.getName().endsWith(".class")) {
                    TransformResult result = transform(data);
                    data = result.data;
                    stringCount += result.stringCount;
                    classCount++;
                }

                ZipEntry copy = new ZipEntry(entry.getName());
                copy.setTime(entry.getTime());
                if (entry.getComment() != null) {
                    copy.setComment(entry.getComment());
                }
                output.putNextEntry(copy);
                output.write(data);
                output.closeEntry();
            }

            ZipEntry decryptor = new ZipEntry(DECRYPTOR_CLASS + ".class");
            output.putNextEntry(decryptor);
            output.write(createDecryptor());
            output.closeEntry();
        }

        Files.move(temporary, jar, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("[string-obfuscator] encrypted " + stringCount + " strings in " + classCount + " classes");
    }

    private static TransformResult transform(byte[] data) {
        ClassReader reader = new ClassReader(data);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        int[] count = {0};

        ClassVisitor visitor = new ClassVisitor(ASM_API, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                             String signature, String[] exceptions) {
                MethodVisitor delegate = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(ASM_API, delegate) {
                    @Override
                    public void visitLdcInsn(Object value) {
                        if (value instanceof String && canEncrypt((String) value)) {
                            super.visitLdcInsn(encrypt((String) value));
                            super.visitMethodInsn(
                                    Opcodes.INVOKESTATIC,
                                    DECRYPTOR_CLASS,
                                    DECRYPTOR_METHOD,
                                    "(Ljava/lang/String;)Ljava/lang/String;",
                                    false
                            );
                            count[0]++;
                        } else {
                            super.visitLdcInsn(value);
                        }
                    }
                };
            }
        };

        reader.accept(visitor, 0);
        return new TransformResult(writer.toByteArray(), count[0]);
    }

    private static boolean canEncrypt(String value) {
        if (value.isEmpty()) {
            return false;
        }

        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (Character.isHighSurrogate(character)) {
                if (i + 1 >= value.length() || !Character.isLowSurrogate(value.charAt(i + 1))) {
                    return false;
                }
                i++;
            } else if (Character.isLowSurrogate(character)) {
                return false;
            }
        }

        int utf8Length = value.getBytes(StandardCharsets.UTF_8).length;
        return utf8Length <= 40000;
    }

    private static String encrypt(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= (byte) (BASE_KEY + i * MULTIPLIER);
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] createDecryptor() {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        writer.visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
                DECRYPTOR_CLASS,
                null,
                "java/lang/Object",
                null
        );

        MethodVisitor method = writer.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                DECRYPTOR_METHOD,
                "(Ljava/lang/String;)Ljava/lang/String;",
                null,
                null
        );
        method.visitCode();
        method.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/util/Base64",
                "getDecoder",
                "()Ljava/util/Base64$Decoder;",
                false
        );
        method.visitVarInsn(Opcodes.ALOAD, 0);
        method.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/util/Base64$Decoder",
                "decode",
                "(Ljava/lang/String;)[B",
                false
        );
        method.visitVarInsn(Opcodes.ASTORE, 1);

        method.visitInsn(Opcodes.ICONST_0);
        method.visitVarInsn(Opcodes.ISTORE, 2);

        Label loop = new Label();
        Label end = new Label();
        method.visitLabel(loop);
        method.visitVarInsn(Opcodes.ILOAD, 2);
        method.visitVarInsn(Opcodes.ALOAD, 1);
        method.visitInsn(Opcodes.ARRAYLENGTH);
        method.visitJumpInsn(Opcodes.IF_ICMPGE, end);

        method.visitVarInsn(Opcodes.ALOAD, 1);
        method.visitVarInsn(Opcodes.ILOAD, 2);
        method.visitVarInsn(Opcodes.ALOAD, 1);
        method.visitVarInsn(Opcodes.ILOAD, 2);
        method.visitInsn(Opcodes.BALOAD);
        method.visitLdcInsn(BASE_KEY);
        method.visitVarInsn(Opcodes.ILOAD, 2);
        method.visitLdcInsn(MULTIPLIER);
        method.visitInsn(Opcodes.IMUL);
        method.visitInsn(Opcodes.IADD);
        method.visitInsn(Opcodes.I2B);
        method.visitInsn(Opcodes.IXOR);
        method.visitInsn(Opcodes.I2B);
        method.visitInsn(Opcodes.BASTORE);

        method.visitIincInsn(2, 1);
        method.visitJumpInsn(Opcodes.GOTO, loop);
        method.visitLabel(end);

        method.visitTypeInsn(Opcodes.NEW, "java/lang/String");
        method.visitInsn(Opcodes.DUP);
        method.visitVarInsn(Opcodes.ALOAD, 1);
        method.visitFieldInsn(
                Opcodes.GETSTATIC,
                "java/nio/charset/StandardCharsets",
                "UTF_8",
                "Ljava/nio/charset/Charset;"
        );
        method.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/lang/String",
                "<init>",
                "([BLjava/nio/charset/Charset;)V",
                false
        );
        method.visitInsn(Opcodes.ARETURN);
        method.visitMaxs(0, 0);
        method.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static byte[] readFully(InputStream stream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = stream.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private static final class TransformResult {
        private final byte[] data;
        private final int stringCount;

        private TransformResult(byte[] data, int stringCount) {
            this.data = data;
            this.stringCount = stringCount;
        }
    }
}
