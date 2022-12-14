package entities;

import java.io.Serializable;
import java.util.Date;

public class Request implements Serializable {

    private int id;
    private Position position;
    private User user;
    private boolean isPassedSoft;
    private boolean isPassedHard;
    private boolean isPassedEnglish;
    private String description;
    private Date dateOfIssue;

    public Request() {
        user = new User();
        dateOfIssue = new Date();
        position = new Position();
    }

    public Request(int id, Position position, User user, boolean isPassedSoft, boolean isPassedHard, boolean isPassedEnglish, String description, Date dateOfIssue) {
        this.id = id;
        this.position = position;
        this.user = user;
        this.isPassedSoft = isPassedSoft;
        this.isPassedHard = isPassedHard;
        this.isPassedEnglish = isPassedEnglish;
        this.description = description;
        this.dateOfIssue = dateOfIssue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPassedSoft() {
        return isPassedSoft;
    }

    public void setPassedSoft(boolean passedSoft) {
        isPassedSoft = passedSoft;
    }

    public boolean isPassedHard() {
        return isPassedHard;
    }

    public void setPassedHard(boolean passedHard) {
        isPassedHard = passedHard;
    }

    public boolean isPassedEnglish() {
        return isPassedEnglish;
    }

    public void setPassedEnglish(boolean passedEnglish) {
        isPassedEnglish = passedEnglish;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request that = (Request) o;

        if (id != that.id) return false;
        if (!position.equals(that.position)) return false;
        if (!dateOfIssue.equals(that.dateOfIssue)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + position.hashCode();
        result = 31 * result + dateOfIssue.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }
}
