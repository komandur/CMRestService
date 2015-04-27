package edu.uw.cmservices.demo;

/**
 * Created by komandur on 4/26/15.
 */
public class CourseInfo {
    private String id;
    private String subjectCode;
    private String courseNumber;
    private Integer credits;
    private String title;
    private String description;

    public CourseInfo() {
    }

    public  CourseInfo(CourseInfo courseInfo) {
        this.setId(courseInfo.getId());
        this.setCourseNumber(courseInfo.getCourseNumber());
        this.setCredits(courseInfo.getCredits());
        this.setDescription(courseInfo.getDescription());
        this.setSubjectCode(courseInfo.getSubjectCode());
        this.setTitle(courseInfo.getTitle());
    }
    public CourseInfo(String id, String subjectCode, String courseNumber,
                      Integer credits, String title, String description) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.courseNumber = courseNumber;
        this.credits = credits;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
