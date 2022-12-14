package entities;

import java.io.Serializable;
import java.util.Date;

public class Employee implements Serializable {

    private Request request;
    private Date dateOfAcceptance;
    private  Float salary;
    private Filial filial;

    public Employee() {
        request = new Request();
        dateOfAcceptance = new Date();
        filial = new Filial();
    }

    public Employee(Request request, Date dateOfAcceptance, Filial filial, float salary) {
        this.request = request;
        this.dateOfAcceptance = dateOfAcceptance;
        this.filial = filial;
        this.salary = salary;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Date getDateOfAcceptance() {
        return dateOfAcceptance;
    }

    public void setDateOfAcceptance(Date dateOfAcceptance) {
        this.dateOfAcceptance.setTime(dateOfAcceptance.getTime());
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee that = (Employee) o;

        if (!request.equals(that.request)) return false;
        return dateOfAcceptance.equals(that.dateOfAcceptance);
    }

    @Override
    public int hashCode() {
        int result = request.hashCode();
        result = 31 * result + dateOfAcceptance.hashCode();
        return result;
    }
}
