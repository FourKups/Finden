package com.fourkups.finden;

import android.net.Uri;

public class CourseModal {

    // variables for our course
    // name and description.
    private String courseContent;
    private Uri courseImage;

    // creating constructor for our variables.
    public CourseModal(Uri courseImage, String courseContent) {
        this.courseImage = courseImage;
        this.courseContent = courseContent;

    }

    // creating getter and setter methods.
    public Uri getCourseImage(){return courseImage;}

    public void setCourseImage(Uri courseImage) {
        this.courseImage = courseImage;
    }

    public String getCourseContent(){return courseContent;}

    public void setCourseContent(String courseContent) { this.courseContent = courseContent; }
}
