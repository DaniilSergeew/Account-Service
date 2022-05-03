package account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "Payments")
@NoArgsConstructor
public class Payment {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "Employee is null or empty")
    private String employee;

    @Column
    @Pattern(regexp = "(0[1-9]|1[0-2])-\\d\\d\\d\\d", message = "Wrong date")
    private String period;

    @Column
    @Min(value = 0, message = "Salary must be non negative!")
    @NotNull(message = "Enter salary")
    private long salary;

    @JsonIgnore
    private static final List<String> MONTHS = List.of("January", "February", "March",
            "April", "May", "June", "July",
            "August", "September", "October",
            "November", "December");

    public String getResponseSalary() {
        return salary / 100 + " dollar(s) " +
                salary % 100 + " cent(s)";
    }

    public String getResponsePeriod() {
        StringBuilder stringBuilder = new StringBuilder(this.period);
        return MONTHS.get(Integer.parseInt(stringBuilder.substring(0, 2)) - 1) + "-" + stringBuilder.substring(3, 7);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return employee.equals(payment.employee) && period.equals(payment.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period);
    }
}