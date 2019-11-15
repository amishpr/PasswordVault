import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CharArrayUtils {

  static void clear(char[] chars) {
    java.util.Arrays.fill(chars, '0');
  }

  static char[] concat(char[] first, char[] second) {
    char[] result = new char[first.length + second.length];

    System.arraycopy(first, 0, result, 0, first.length);
    System.arraycopy(second, 0, result, first.length, second.length);

    clear(first);
    clear(second);

    return result;
  }

  // https://stackoverflow.com/a/43996428
  static byte[] charsToBytes(char[] chars)
  {
    final ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(chars));
    return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
  }

  // https://stackoverflow.com/a/43996428
  static char[] bytesToChars(byte[] bytes)
  {
    final CharBuffer charBuffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes));
    return Arrays.copyOf(charBuffer.array(), charBuffer.limit());
  }

}
