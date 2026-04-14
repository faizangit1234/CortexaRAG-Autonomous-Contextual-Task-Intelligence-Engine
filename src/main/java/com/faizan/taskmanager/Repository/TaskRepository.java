package com.faizan.taskmanager.Repository;

import com.faizan.taskmanager.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
}
