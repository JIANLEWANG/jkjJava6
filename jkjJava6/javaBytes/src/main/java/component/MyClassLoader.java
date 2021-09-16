package component;

import java.io.IOException;
import java.io.InputStream;


/**
 * 有个问题：
 * 可以看出byte[]解密和第一个数是-54即对应二进制为10110110
 * 而10110110对应的16进制为b6.而不是ca（因为正确的编译文件开头是以cafebabe）
 * 而ca对应的byte是-74。
 * ----所以解密出来的-54不应该是源文件真实的字节流数值才对。
 * <p>
 * -74的补码是-54 -54的补码是-74
 * <p>
 * 所以有一个猜想：即，文件本身的二进制（202）在读了以后转化成byte是-54，因为byte的范围只是在-128~127
 * 那么同理在写文件的时候，byte=-54写入文件的时候，文件真实的二进制数应该是当前byte再进行一轮补码操作转为二进制存储
 * <p>
 * 正解：
 */

/**
 * 自定义类加载器
 */
public class MyClassLoader extends ClassLoader {
    private byte[] bytes;
    private static final Integer KEY = 255;


    public static void main(String args[]) {
        MyClassLoader myClassLoader = new MyClassLoader();
        myClassLoader.readEncryptedFile("Hello.xlass");
        myClassLoader.invoke("Hello", "hello");
    }


    /**
     * 编译文件内部调用
     * @param className 类名
     * @param methodName 方法名
     * @return null 调用失败
     */
    public Object invoke(String className, String methodName) {
        try {
            Object instance = findClass(className).newInstance();
            return instance.getClass().getMethod(methodName).invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void readEncryptedFile(String path) {

        InputStream inputStream = null;
        DynamicBytesArray bytesArray = new DynamicBytesArray();
        int content;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (inputStream == null) {
                return;
            }
            while ((content = inputStream.read()) != -1) {
                //获取255-x后的值
                int revertByteInt = decrypt(content);
                bytesArray.add((byte) revertByteInt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.bytes = bytesArray.getBytes();
    }


    /**
     * y=255-x
     *
     * @param singleByte 加密数据
     * @return 返回解密数据
     */
    private Integer decrypt(int singleByte) {
        return KEY - singleByte;
    }


    @Override
    protected Class<?> findClass(String name) {
        return defineClass(name, bytes, 0, bytes.length);
    }


}
