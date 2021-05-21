package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.TaskService;
import com.codesoom.assignment.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TaskController 클래스")
class TaskControllerTest {

    private TaskController taskController;
    private TaskService taskService;

    private Task generateTask(long id, String title) {
        Task newTask = new Task();
        newTask.setId(id);
        newTask.setTitle(title);
        return newTask;
    }

    private TaskService generateTaskService(long taskCount) {
        TaskService newTaskService = new TaskService();
        for (long i = 1L; i <= taskCount; i++) {
            Task newTask = generateTask(i, String.format("task%d", i));
            newTaskService.createTask(newTask);
        }
        return newTaskService;
    }

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
        taskController = new TaskController(taskService);
    }

    @Nested
    @DisplayName("list 메소드는")
    class Describe_of_list {

        @Nested
        @DisplayName("만약 tasks가 비어있다면")
        class Context_of_empty_tasks {

            @Test
            @DisplayName("비어있는 배열을 반환한다")
            void it_returns_empty_array() {
                List<Task> tasks = taskController.list();
                assertThat(tasks).isEmpty();
            }
        }

        @Nested
        @DisplayName("만약 tasks가 비어있지 않다면")
        class Context_of_not_empty_tasks {

            private TaskService size1;
            private TaskService size2;
            private TaskService size100;
            private TaskController controller1;
            private TaskController controller2;
            private TaskController controller100;

            @BeforeEach
            void setTasksNotEmpty() {
                size1 = generateTaskService(1);
                size2 = generateTaskService(2);
                size100 = generateTaskService(100);

                controller1 = new TaskController(size1);
                controller2 = new TaskController(size2);
                controller100 = new TaskController(size100);
            }

            @Test
            @DisplayName("객체 배열을 반환한다")
            void it_returns_task_array() {
                List<Task> tasks = controller1.list();
                assertThat(tasks)
                        .hasSize(1);

                tasks = controller2.list();
                assertThat(tasks)
                        .hasSize(2);

                tasks = controller100.list();
                assertThat(tasks)
                        .hasSize(100);
            }
        }
    }

    @Nested
    @DisplayName("getTask 메서드는")
    class Describe_of_getTask {

        private Task givenTask;

        @BeforeEach
        void appendSourceToTasks() {
            this.givenTask = generateTask(1L, "task1");
            givenTask = taskService.createTask(givenTask);
        }

        @Nested
        @DisplayName("만약 유효한 id가 인자로 주어지면")
        class Context_of_valid_id {

            private long validId;

            @BeforeEach
            void setValidId() {
                validId = givenTask.getId();
            }

            @Test
            @DisplayName("id에 해당하는 객체를 반환한다")
            void it_returns_task() {
                Task task = taskController.detail(validId);
                assertThat(task)
                        .isEqualTo(givenTask);
            }
        }

        @Nested
        @DisplayName("만약 유효하지 않은 id가 인자로 주어지면")
        class Context_of_invalid_id {

            private long invalidId;

            @BeforeEach
            void setInvalidId() {
                taskController.delete(givenTask.getId());
                invalidId = givenTask.getId();
            }

            @Test
            @DisplayName("'Task not found' 메시지를 담은 예외를 던진다")
            void it_throws_exception() {
                Throwable thrown = catchThrowable(() -> { taskController.detail(invalidId); });
                assertThat(thrown)
                        .hasMessageContaining("Task not found");
            }
        }
    }

    @Nested
    @DisplayName("create 메소드는")
    class Describe_of_create {

        @Nested
        @DisplayName("만약 Task 객체가 인자로 주어지면")
        class Context_of_valid_task_object {

            private Task givenTask;

            @BeforeEach
            void setSource() {
                this.givenTask = generateTask(1L, "task1");
            }

            @Test
            @DisplayName("객체를 추가하고 추가한 객체를 반환한다")
            void it_returns_task_appending_task_to_tasks() {
                Task createdTask = taskController.create(givenTask);
                assertThat(createdTask)
                        .isEqualTo(givenTask)
                        .withFailMessage("추가한 객체를 반환하지 않았다");

                createdTask = taskController.detail(givenTask.getId());
                assertThat(createdTask)
                        .isEqualTo(givenTask)
                        .withFailMessage("객체가 추가되지 않았다");
            }
        }
    }

    @Nested
    @DisplayName("update 메소드는")
    class Describe_of_update {

        @Nested
        @DisplayName("만약 유효한 id와 Task 객체가 인자로 주어지면")
        class Context_of_valid_id_and_task {

            private Task givenTask;
            private Task destTask;

            @BeforeEach
            void createTaskAnd() {
                this.givenTask = generateTask(1L, "givenTask");
                this.destTask = generateTask(1L, "destTask");

                taskService.createTask(givenTask);
            }

            @Test
            @DisplayName("객체를 갱신하고, 갱신한 객체를 반환한다")
            void it_returns_task_updating_it() {
                Task updatedTask = taskController.update(givenTask.getId(), destTask);
                assertThat(updatedTask)
                        .isEqualTo(destTask)
                        .withFailMessage("객체한 객체가 반환되지 않았다");

                updatedTask = taskController.detail(givenTask.getId());
                assertThat(updatedTask)
                        .isEqualTo(destTask)
                        .withFailMessage("객체가 갱신되지 않았다");
            }
        }
    }

    @Nested
    @DisplayName("patch 메소드는")
    class Describe_of_patch {

        @Nested
        @DisplayName("만약 유효한 id와 Task 객체가 인자로 주어지면")
        class Context_of_valid_id_and_task {

            private Task givenTask;
            private Task destTask;

            @BeforeEach
            void createTaskAnd() {
                this.givenTask = generateTask(1L, "givenTask");
                this.destTask = generateTask(1L, "destTask");

                taskService.createTask(givenTask);
            }

            @Test
            @DisplayName("주어진 인자에 따라 객체를 갱신하다")
            void it_returns_task_updating_it() {
                Task updatedTask = taskController.update(givenTask.getId(), destTask);
                assertThat(updatedTask)
                        .isEqualTo(destTask)
                        .withFailMessage("갱신한 객체가 반환되지 않았다");

                updatedTask = taskController.detail(givenTask.getId());
                assertThat(updatedTask)
                        .isEqualTo(destTask)
                        .withFailMessage("객체가 갱신되지 않았다");
            }
        }
    }

    @Nested
    @DisplayName("delete 메소드는")
    class Describe_of_delete {

        @Nested
        @DisplayName("만약 유효한 id가 인자로 주어지면")
        class Context_of_valid_id {

            private Task task1;
            private Task task2;
            private long validId;

            @BeforeEach
            void setSources() {
                task1 = generateTask(1L, "task1");
                task2 = generateTask(2L, "task2");

                taskService.createTask(task1);
                taskService.createTask(task2);
                validId = task1.getId();
            }

            @Test
            @DisplayName("해당 id 객체를 tasks에서 삭제하고, 아무 값도 반환하지 않는다")
            void it_returns_noting() {
                taskController.delete(validId);

                assertThat(taskController.list())
                        .hasSize(1)
                        .doesNotContain(task1);
            }
        }
    }

}
