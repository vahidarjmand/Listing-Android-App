package tmediaa.ir.ahamdian.otto;

/**
 * Created by tmediaa on 8/20/2017.
 */

public class AppEvents {
    public static class sendOrderID {

        private int id;

        public sendOrderID(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }

}
