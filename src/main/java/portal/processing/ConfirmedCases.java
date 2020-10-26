package portal.processing;

import java.util.Date;

public class ConfirmedCases {
    Integer cases;
    Date date;

    public ConfirmedCases(Integer cases, Date date) {
        this.cases = cases;
        this.date = date;
    }

    public Integer getCases() {
        return cases;
    }


    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfirmedCases)) {
            return false;
        }
        ConfirmedCases c = (ConfirmedCases) o;
        return cases.equals(c.cases) && date.equals(c.date);
    }

    //Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + cases.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }

}

