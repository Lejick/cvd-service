package portal;

public class ResultDTO {
    String country;
    Integer min;
    Integer max;

    public ResultDTO(String country, Integer min, Integer max) {
        this.country = country;
        this.min = min;
        this.max = max;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }
}
