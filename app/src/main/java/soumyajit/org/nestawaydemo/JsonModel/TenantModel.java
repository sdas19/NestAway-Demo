package soumyajit.org.nestawaydemo.JsonModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Soumyajit Das on 30-01-2018.
 */

public class TenantModel {

    private List<Details> slot_booking;

    public TenantModel(List<Details> slot_booking) {
        this.slot_booking = slot_booking;
    }

    public List<Details> getSlot_booking() {
        return slot_booking;
    }

    public static class Details implements Serializable{
        private String name;
        private String email;
        private String phone;
        private String time;
        private String date;
        private String visit_status;

        public Details(String name, String email, String phone, String time, String date, String visit_status) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.time = time;
            this.date = date;
            this.visit_status = visit_status;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getTime() {
            return time;
        }

        public String getDate() {
            return date;
        }

        public String getVisit_status() {
            return visit_status;
        }

        // getters and setters
    }

}
