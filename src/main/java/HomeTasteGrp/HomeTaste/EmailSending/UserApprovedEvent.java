package HomeTasteGrp.HomeTaste.EmailSending;

import org.springframework.context.ApplicationEvent;

public class UserApprovedEvent extends ApplicationEvent {
    private String userEmail;

    public UserApprovedEvent(Object source, String userEmail) {
        super(source);
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
