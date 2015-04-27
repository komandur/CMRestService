package edu.uw.cmservices.demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by komandur on 4/22/15.
 */
@RestController
@RequestMapping("/course")
public class CmServiceController {

    private List<CourseInfo> courseDB;

    public CmServiceController() {
        courseDB = new ArrayList<>();
        initCourseDB();
    }

    @RequestMapping(method=RequestMethod.GET)
    public CourseInfo getCourse(@RequestParam(value="id") String courseId ) {
        CourseInfo courseInfo = findCourse(courseId);
        if(courseInfo != null) {
            return courseInfo;
        }
        throw new CourseServiceException(String.format("courseId:%s not found", courseId));
    }

    @RequestMapping(value = "/search", method=RequestMethod.GET)
    public List<CourseInfo> searchCourses(@RequestParam(value="s") String  searchString) {
        List<CourseInfo> courseInfos =  findCourses(searchString);
        if(!courseInfos.isEmpty()) {
            return courseInfos;
        }
        throw new CourseServiceException(String.format("searchString:%s could not be found in title, subjectCode or courseNumber", searchString));
    }

    @RequestMapping(method=RequestMethod.POST)
    public CourseInfo createCourse(@RequestBody CourseInfo courseInfoNew) {
        CourseInfo courseInfoExists = this.findCourse(courseInfoNew.getId());
        if(courseInfoExists != null) {
            throw new CourseServiceException(String.format("Course with courseId:%s already exists!", courseInfoNew.getId()));
        }
        // validation
        Errors errors = validateCourseInfo(courseInfoNew);
        if(!errors.errorMessages.isEmpty()) {
            throw new CourseServiceException(String.format("Validation failed ...%s", errors.errorMessages.toString()));
        }
        CourseInfo courseInfo = new CourseInfo(courseInfoNew);
        courseDB.add(courseInfo);
        return courseInfoNew;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteCourse(@RequestParam(value="id") String courseId ) {

        Iterator<CourseInfo> courseInfoIterator = courseDB.iterator();
        Boolean found = Boolean.FALSE;

        while (courseInfoIterator.hasNext()) {
            CourseInfo courseInfo = courseInfoIterator.next();
            if (courseInfo.getId().equals(courseId)) {
                courseInfoIterator.remove();
                found = Boolean.TRUE;
            }
        }
        if(!found) {
            throw new CourseServiceException(String.format("courseId:%s not found and could not be deleted", courseId));
        }
    }

    @RequestMapping(method=RequestMethod.PUT)
    public CourseInfo updateCourse(@RequestParam(value="id") String courseId, @RequestBody CourseInfo courseInfoUpdated) {
        // validation
        Errors errors = validateCourseInfo(courseInfoUpdated);
        if(!errors.errorMessages.isEmpty()) {
            throw new CourseServiceException(String.format("Validation failed ...%s", errors.errorMessages.toString()));
        }
        CourseInfo courseInfoCurrent = this.findCourse(courseId);
        // save to repository
        return saveToRepository(courseInfoCurrent, courseInfoUpdated);
    }

    private class CourseServiceException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        public CourseServiceException(String msg)
        {
            super(msg);
        }
    }

    private class Errors {
        ArrayList<String> errorMessages;

        public Errors() {
            errorMessages = new ArrayList<>();
        }
    }


    @ExceptionHandler(CourseServiceException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleResourceNotFoundException(CourseServiceException ex)
    {
        return ex.getMessage();
    }

    private CourseInfo saveToRepository(CourseInfo courseInfoOriginal, CourseInfo courseInfoUpdated) {
        courseInfoOriginal.setId(courseInfoUpdated.getId());
        courseInfoOriginal.setCourseNumber(courseInfoUpdated.getCourseNumber());
        courseInfoOriginal.setCredits(courseInfoUpdated.getCredits());
        courseInfoOriginal.setDescription(courseInfoUpdated.getDescription());
        courseInfoOriginal.setSubjectCode(courseInfoUpdated.getSubjectCode());
        courseInfoOriginal.setTitle(courseInfoUpdated.getTitle());
        // Save to repository
        return courseInfoOriginal;
    }

    private List<CourseInfo> findCourses(String searchString) {
        List<CourseInfo> courseInfos = new ArrayList<>();

        for (CourseInfo courseInfo : courseDB) {
            if (courseInfo.getCourseNumber().contains(searchString)
                || courseInfo.getSubjectCode().contains(searchString)
                || courseInfo.getTitle().contains(searchString)) {
                courseInfos.add(courseInfo);
            }
        }
        return courseInfos;
    }

    private CourseInfo findCourse(String courseId) {

        for (CourseInfo courseInfo : courseDB) {
            if (courseInfo.getId().equals(courseId)) {
                return courseInfo;
            }
        }
        return null;
    }

    // Example validation, in reality return an Error class
    private Errors validateCourseInfo(CourseInfo courseInfo) {

        Errors errors = new Errors();
        if (courseInfo.getId() == null) {
            errors.errorMessages.add("courseId cannot be null");
        }
        return errors;
    }

    private void initCourseDB() {

        JSONParser parser = new JSONParser();

        try {
            JSONArray courseJSONDB;
            ObjectMapper mapper = new ObjectMapper();

            ClassPathResource cpr = new ClassPathResource("courseDB.txt");
            courseJSONDB = (JSONArray) parser.parse(new BufferedReader(new InputStreamReader(cpr.getInputStream())));
            try {
                courseDB.addAll(Arrays.asList(mapper.readValue(courseJSONDB.toJSONString(), CourseInfo[].class)));
            } catch (com.fasterxml.jackson.core.JsonParseException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}