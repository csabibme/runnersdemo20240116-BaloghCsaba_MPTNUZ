package hu.gde.runnersdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/runner")
public class RunnerRestController {

    @Autowired
    private LapTimeRepository lapTimeRepository;
    private RunnerRepository runnerRepository;

    @Autowired
    public RunnerRestController(RunnerRepository runnerRepository, LapTimeRepository lapTimeRepository) {
        this.runnerRepository = runnerRepository;
        this.lapTimeRepository = lapTimeRepository;
    }

    @GetMapping("/{id}")
    public RunnerEntity getRunner(@PathVariable Long id) {
        return runnerRepository.findById(id).orElse(null);
    }

    @GetMapping("/{id}/averagelaptime")
    public double getAverageLaptime(@PathVariable Long id) {
        RunnerEntity runner = runnerRepository.findById(id).orElse(null);
        if (runner != null) {
            List<LapTimeEntity> laptimes = runner.getLaptimes();
            int totalTime = 0;
            for (LapTimeEntity laptime : laptimes) {
                totalTime += laptime.getTimeSeconds();
            }
            double averageLaptime = (double) totalTime / laptimes.size();
            return averageLaptime;
        } else {
            return -1.0;
        }
    }
    @GetMapping("/maxshoesize")
    public ResponseEntity<String> getRunnerNameWithMaxShoeSize() {
        RunnerEntity runnerWithMaxShoeSize = runnerRepository.findFirstByOrderByShoesizeDesc();
        if (runnerWithMaxShoeSize != null) {
            String runnerName = runnerWithMaxShoeSize.getRunnerName();
            return ResponseEntity.ok(runnerName);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    public List<RunnerEntity> getAllRunners() {
        return runnerRepository.findAll();
    }

    @PostMapping("/{id}/addlaptime")
    public ResponseEntity addLaptime(@PathVariable Long id, @RequestBody LapTimeRequest lapTimeRequest) {
        RunnerEntity runner = runnerRepository.findById(id).orElse(null);
        if (runner != null) {
            LapTimeEntity lapTime = new LapTimeEntity();
            lapTime.setTimeSeconds(lapTimeRequest.getLapTimeSeconds());
            lapTime.setLapNumber(runner.getLaptimes().size() + 1);
            lapTime.setRunner(runner);
            lapTimeRepository.save(lapTime);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Runner with ID " + id + " not found");
        }
    }
    public static class LapTimeRequest {
        private int lapTimeSeconds;

        public int getLapTimeSeconds() {
            return lapTimeSeconds;
        }

        public void setLapTimeSeconds(int lapTimeSeconds) {
            this.lapTimeSeconds = lapTimeSeconds;
        }
    }
    @PostMapping("/{id}/changeshoe")
    public ResponseEntity<String> changeRunnerShoe(@PathVariable Long id, @RequestBody String newShoeName) {
        RunnerEntity runner = runnerRepository.findById(id).orElse(null);

        if (runner != null) {
            // Assuming a runner can have only one shoe at a time
            List<ShoeNameEntity> shoes = runner.getShoeNames();

            if (shoes.isEmpty()) {
                // If the runner doesn't have a shoe, create a new ShoeNameEntity
                ShoeNameEntity newShoe = new ShoeNameEntity();
                newShoe.setShoeName(newShoeName);
                newShoe.setRunner(runner);
                shoes.add(newShoe);
            } else {
                // If the runner already has a shoe, update the existing one
                ShoeNameEntity currentShoe = shoes.get(0);
                currentShoe.setShoeName(newShoeName);
            }

            runnerRepository.save(runner);
            return ResponseEntity.ok("Runner's shoe changed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
