import org.mindrot.jbcrypt.BCrypt;

public class HashChecker {
    public static void main(String[] args) {
        System.out.println("New hash: " +
                BCrypt.hashpw("patient123", BCrypt.gensalt(12)));
    }
}
