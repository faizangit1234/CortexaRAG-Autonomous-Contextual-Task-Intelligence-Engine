package com.faizan.taskmanager.Dto;

import lombok.Data;

@Data
public class AIResponse {
    private Message message;

    public static class Message{
        private String role;

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

        private String content;
    }


}
