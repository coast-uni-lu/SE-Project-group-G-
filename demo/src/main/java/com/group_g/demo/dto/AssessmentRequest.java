
package com.group_g.demo.dto;
import java.util.Map;

    // data sent from frontend to backend when user submits assessment
public class AssessmentRequest {

    private String nickname;
    private String email;

    private Map<String, Integer> answers;

    public String getNickname() {
        return nickname;
    }

    public Map<String, Integer> getAnswers() {
        return answers;
    }

    public String getEmail() {
        return email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAnswers(Map<String, Integer> answers) {
        this.answers = answers;
    }
}
