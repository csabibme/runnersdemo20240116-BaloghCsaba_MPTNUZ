package hu.gde.runnersdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RunnerController {

    @Autowired
    private RunnerRepository runnerRepository;
    @Autowired
    private LapTimeRepository lapTimeRepository;
    @GetMapping("/runners")
    public String getAllRunners(Model model) {
        List<RunnerEntity> runners = runnerRepository.findAll();
        double averagePace = runners.stream()
                .mapToLong(RunnerEntity::getPace)
                .average()
                .orElse(0.0);
        model.addAttribute("runners", runners);
        model.addAttribute("averagePace", averagePace);
        return "runners";
    }

    @GetMapping("/runner/{id}")
    public String getRunnerById(@PathVariable Long id, Model model) {
        RunnerEntity runner = runnerRepository.findById(id).orElse(null);
        RunnerService runnerService = new RunnerService(runnerRepository);
        if (runner != null) {
            model.addAttribute("runner", runner);
            double averageLaptime = runnerService.getAverageLaptime(runner.getRunnerId());
            model.addAttribute("averageLaptime", averageLaptime);
            return "runner";
        } else {
            return "error";
        }
    }

    @GetMapping("/runner/{id}/addlaptime")
    public String showAddLaptimeForm(@PathVariable Long id, Model model) {
        RunnerEntity runner = runnerRepository.findById(id).orElse(null);
        if (runner != null) {
            model.addAttribute("runner", runner);
            LapTimeEntity laptime = new LapTimeEntity();
            laptime.setLapNumber(runner.getLaptimes().size() + 1);
            model.addAttribute("laptime", laptime);
            return "addlaptime";
        } else {
            return "error";
        }
    }

    @GetMapping("/runner/{id}/changeshoe")
    public String showChangeShoeForm(@PathVariable Long id, Model model) {
        RunnerEntity runner = runnerRepository.findById(id).orElse(null);
        if (runner != null) {
            model.addAttribute("runner", runner);
            model.addAttribute("newShoeName", "");
            return "changeshoe";
        } else {
            return "Runner is not found error.";
        }
    }

    @PostMapping("/runner/{id}/addlaptime")
    public String addLaptime(@PathVariable Long id, @ModelAttribute LapTimeEntity laptime) {
        RunnerEntity runner = runnerRepository.findById(id).orElse(null);
        if (runner != null) {
            laptime.setRunner(runner);
            laptime.setId(null);
            runner.getLaptimes().add(laptime);
            runnerRepository.save(runner);
        } else {
            return "error";
        }
        return "redirect:/runner/" + id;
    }

    @PostMapping("/runner/{id}/changeshoe")
    public String changeRunnerShoe(@PathVariable Long id, @RequestParam String newShoeName) {
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
        } else {
            // Handle error when runner is not found
        }
        return "redirect:/runner/" + id;
    }

}

