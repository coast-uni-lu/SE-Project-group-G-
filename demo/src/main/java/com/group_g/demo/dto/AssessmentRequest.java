
package com.group_g.demo.dto;
import java.util.Map;

    // data sent from frontend to backend when user submits assessment
public class AssessmentRequest {

    private String nickname;

    private Map<String, Integer> answers;

    public String getNickname() {
        return nickname;
    }

    public Map<String, Integer> getAnswers() {
        return answers;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAnswers(Map<String, Integer> answers) {
        this.answers = answers;
    }
}
