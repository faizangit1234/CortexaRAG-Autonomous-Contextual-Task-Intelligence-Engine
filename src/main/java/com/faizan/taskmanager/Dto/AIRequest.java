package com.faizan.taskmanager.Dto;


import lombok.Data;

import java.util.List;

@Data
public class AIRequest {

    private String model;
    private List<Message> messages;
    private boolean stream;

    public static class Message{
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        private String role;
        private String content;
    }


}
