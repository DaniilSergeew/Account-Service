package account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Logs")
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int id;
    @Column
    private String date;
    @Column
    private Action action;
    @Column
    private String subject;
    @Column
    private String object;
    @Column
    private String path;

    public Event(Action action,
                 String subject,
                 String object,
                 HttpServletRequest request) {
        date = LocalDateTime.now().toString();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = request.getRequestURI();
    }
}