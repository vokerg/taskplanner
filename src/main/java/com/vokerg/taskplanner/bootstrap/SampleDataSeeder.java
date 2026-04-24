package com.vokerg.taskplanner.bootstrap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vokerg.taskplanner.model.Project;
import com.vokerg.taskplanner.model.Task;
import com.vokerg.taskplanner.model.TaskPriority;
import com.vokerg.taskplanner.model.TaskStatus;
import com.vokerg.taskplanner.repository.ProjectRepository;
import com.vokerg.taskplanner.repository.TaskRepository;

@Component
public class SampleDataSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataSeeder.class);

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public SampleDataSeeder(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (this.projectRepository.count() > 0 || this.taskRepository.count() > 0) {
            LOGGER.info("Skipping sample data seeding because data already exists.");
            return;
        }

        Instant now = Instant.now();

        Project websiteRedesign = createProject(
            "Website redesign",
            "Refresh the marketing site, improve navigation, and ship a faster landing page.",
            false,
            now.minus(10, ChronoUnit.DAYS)
        );
        Project mobileAppLaunch = createProject(
            "Mobile app launch",
            "Coordinate beta feedback, polish onboarding, and prepare the first public release.",
            false,
            now.minus(8, ChronoUnit.DAYS)
        );
        Project opsAutomation = createProject(
            "Ops automation",
            "Reduce manual support and deployment tasks with scripts and dashboards.",
            false,
            now.minus(6, ChronoUnit.DAYS)
        );
        Project hiringPipeline = createProject(
            "Hiring pipeline",
            "Improve candidate flow, scheduling, and scorecard consistency.",
            true,
            now.minus(15, ChronoUnit.DAYS)
        );
        Project quarterlyPlanning = createProject(
            "Quarterly planning",
            "Gather roadmap inputs and align the next quarter across product and engineering.",
            false,
            now.minus(4, ChronoUnit.DAYS)
        );
        Project knowledgeBase = createProject(
            "Knowledge base cleanup",
            "Archive stale docs and improve searchability for the support team.",
            false,
            now.minus(3, ChronoUnit.DAYS)
        );

        this.projectRepository.saveAll(List.of(
            websiteRedesign,
            mobileAppLaunch,
            opsAutomation,
            hiringPipeline,
            quarterlyPlanning,
            knowledgeBase
        ));

        this.taskRepository.saveAll(List.of(
            createTask(websiteRedesign, "Audit current pages", "Document weak content and broken user flows.", TaskStatus.DONE, TaskPriority.MEDIUM, now.minus(9, ChronoUnit.DAYS), now.plus(1, ChronoUnit.DAYS)),
            createTask(websiteRedesign, "Design new hero section", "Create responsive concepts for the homepage hero.", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, now.minus(6, ChronoUnit.DAYS), now.plus(3, ChronoUnit.DAYS)),
            createTask(websiteRedesign, "Update pricing copy", "Rewrite pricing explanations with clearer feature comparisons.", TaskStatus.TODO, TaskPriority.MEDIUM, now.minus(2, ChronoUnit.DAYS), now.plus(7, ChronoUnit.DAYS)),
            createTask(websiteRedesign, "Optimize image assets", "Compress large assets and convert legacy formats.", TaskStatus.TODO, TaskPriority.LOW, now.minus(1, ChronoUnit.DAYS), now.plus(10, ChronoUnit.DAYS)),

            createTask(mobileAppLaunch, "Review beta feedback", "Triage tester feedback and identify top launch blockers.", TaskStatus.DONE, TaskPriority.HIGH, now.minus(7, ChronoUnit.DAYS), now.plus(2, ChronoUnit.DAYS)),
            createTask(mobileAppLaunch, "Polish onboarding screens", "Tighten copy and spacing in the first-run experience.", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, now.minus(5, ChronoUnit.DAYS), now.plus(4, ChronoUnit.DAYS)),
            createTask(mobileAppLaunch, "Prepare release notes", "Draft a user-friendly summary of launch improvements.", TaskStatus.TODO, TaskPriority.MEDIUM, now.minus(2, ChronoUnit.DAYS), now.plus(6, ChronoUnit.DAYS)),
            createTask(mobileAppLaunch, "Record demo video", "Capture a short walkthrough for social and support channels.", TaskStatus.TODO, TaskPriority.LOW, now.minus(1, ChronoUnit.DAYS), now.plus(12, ChronoUnit.DAYS)),

            createTask(opsAutomation, "Script log rotation checks", "Automate daily verification of log rotation on shared hosts.", TaskStatus.DONE, TaskPriority.MEDIUM, now.minus(5, ChronoUnit.DAYS), now.plus(1, ChronoUnit.DAYS)),
            createTask(opsAutomation, "Build deploy dashboard", "Show deploy duration, failures, and rollback counts.", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, now.minus(4, ChronoUnit.DAYS), now.plus(5, ChronoUnit.DAYS)),
            createTask(opsAutomation, "Alert on queue backlog", "Notify the team when background jobs start piling up.", TaskStatus.TODO, TaskPriority.HIGH, now.minus(2, ChronoUnit.DAYS), now.plus(8, ChronoUnit.DAYS)),
            createTask(opsAutomation, "Clean cron documentation", "Make recurring jobs easier to discover and maintain.", TaskStatus.TODO, TaskPriority.LOW, now.minus(1, ChronoUnit.DAYS), now.plus(14, ChronoUnit.DAYS)),

            createTask(hiringPipeline, "Standardize interview scorecards", "Align rubrics across interviewers and roles.", TaskStatus.DONE, TaskPriority.HIGH, now.minus(14, ChronoUnit.DAYS), now.minus(8, ChronoUnit.DAYS)),
            createTask(hiringPipeline, "Set up scheduling templates", "Speed up panel coordination with reusable templates.", TaskStatus.DONE, TaskPriority.MEDIUM, now.minus(13, ChronoUnit.DAYS), now.minus(7, ChronoUnit.DAYS)),
            createTask(hiringPipeline, "Document offer process", "Capture the steps from final approval to candidate response.", TaskStatus.DONE, TaskPriority.MEDIUM, now.minus(12, ChronoUnit.DAYS), now.minus(6, ChronoUnit.DAYS)),
            createTask(hiringPipeline, "Close feedback loop", "Ensure every candidate receives a timely outcome update.", TaskStatus.DONE, TaskPriority.LOW, now.minus(11, ChronoUnit.DAYS), now.minus(5, ChronoUnit.DAYS)),

            createTask(quarterlyPlanning, "Collect roadmap proposals", "Ask each team for candidate goals and supporting context.", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, now.minus(3, ChronoUnit.DAYS), now.plus(4, ChronoUnit.DAYS)),
            createTask(quarterlyPlanning, "Estimate engineering effort", "Rough-size the largest initiatives before prioritization.", TaskStatus.TODO, TaskPriority.HIGH, now.minus(2, ChronoUnit.DAYS), now.plus(9, ChronoUnit.DAYS)),
            createTask(quarterlyPlanning, "Draft leadership review", "Prepare a concise briefing deck for tradeoff discussion.", TaskStatus.TODO, TaskPriority.MEDIUM, now.minus(1, ChronoUnit.DAYS), now.plus(11, ChronoUnit.DAYS)),
            createTask(quarterlyPlanning, "Publish final plan", "Share commitments, owners, and target dates company-wide.", TaskStatus.TODO, TaskPriority.LOW, now, now.plus(16, ChronoUnit.DAYS)),

            createTask(knowledgeBase, "Archive outdated articles", "Remove stale pages and redirect readers to current docs.", TaskStatus.DONE, TaskPriority.LOW, now.minus(2, ChronoUnit.DAYS), now.plus(1, ChronoUnit.DAYS)),
            createTask(knowledgeBase, "Tag high-traffic guides", "Improve discoverability for the most-used support articles.", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, now.minus(1, ChronoUnit.DAYS), now.plus(5, ChronoUnit.DAYS)),
            createTask(knowledgeBase, "Rewrite setup checklist", "Clarify first-time setup steps with screenshots.", TaskStatus.TODO, TaskPriority.HIGH, now, now.plus(7, ChronoUnit.DAYS)),
            createTask(knowledgeBase, "Add ownership notes", "List the responsible team for each critical document.", TaskStatus.TODO, TaskPriority.MEDIUM, now, now.plus(13, ChronoUnit.DAYS))
        ));

        LOGGER.info("Seeded {} sample projects and {} sample tasks.", 6, 24);
    }

    private Project createProject(String title, String description, boolean completed, Instant createdAt) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setCompleted(completed);
        project.setCreatedAt(createdAt);
        return project;
    }

    private Task createTask(
        Project project,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant createdAt,
        Instant dueDate
    ) {
        Task task = new Task();
        task.setProject(project);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        task.setCreatedAt(createdAt);
        task.setDueDate(dueDate);
        return task;
    }
}
