package hu.gde.runnersdemo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RunnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long runnerId;
    private String runnerName;
    private long pace;
    private int shoesize;

    @OneToMany(mappedBy = "runner")
    private List<LapTimeEntity> laptimes = new ArrayList<>();

    @OneToMany(mappedBy = "runner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoeNameEntity> shoeNames = new ArrayList<>();

    public RunnerEntity() {
    }

    public long getRunnerId() {
        return runnerId;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public long getPace() {
        return pace;
    }

    public void setRunnerId(long runnerId) {
        this.runnerId = runnerId;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public void setPace(long pace) {
        this.pace = pace;
    }

    public List<LapTimeEntity> getLaptimes() {
        return laptimes;
    }

    public int getShoesize() { return shoesize; }
    public void setShoesize(int shoesize) {
        this.shoesize = shoesize;
    }

    public List<ShoeNameEntity> getShoeNames() { return shoeNames;
    }
    public void setShoeNames(List<ShoeNameEntity> shoeNames) {
        this.shoeNames = shoeNames;
    }

}

