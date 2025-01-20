package com.studentmanagement.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.studentmanagement.entity.Assignment;
import com.studentmanagement.entity.AssignmentDetails;
import com.studentmanagement.entity.Course;
import com.studentmanagement.entity.GradeDetails;
import com.studentmanagement.entity.Student;
import com.studentmanagement.entity.StudentCourseDetails;
import com.studentmanagement.entity.Teacher;
import com.studentmanagement.service.AssignmentDetailsService;
import com.studentmanagement.service.AssignmentService;
import com.studentmanagement.service.CourseService;
import com.studentmanagement.service.GradeDetailsService;
import com.studentmanagement.service.StudentCourseDetailsService;
import com.studentmanagement.service.TeacherService;


@Controller
@RequestMapping("/teacher")
public class TeacherController {
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private TeacherService teacherService;
	
	@Autowired
	private StudentCourseDetailsService studentCourseDetailsService;
	

	
	@Autowired
	private GradeDetailsService gradeDetailsService;
	
	@GetMapping("/{teacherId}/courses")
	public String showTeacherCourses(@PathVariable("teacherId") int teacherId, Model theModel) {
		Teacher teacher = teacherService.findByTeacherId(teacherId);
		List<Course> courses = teacher.getCourses();
		
		theModel.addAttribute("teacher", teacher);
		theModel.addAttribute("courses", courses);
		return "teacher/teacher-courses";
	}
	
	@GetMapping("/{teacherId}/courses/{courseId}")
	public String showTeacherCourseDetails(@PathVariable("teacherId") int teacherId, @PathVariable("courseId") int courseId, Model theModel) {
		Teacher teacher = teacherService.findByTeacherId(teacherId);
		Course course = courseService.findCourseById(courseId);
		List<Course> courses = teacher.getCourses();
		List<Student> students = course.getStudents();
		
		if(students.size() != 0) {
			List<Assignment> assignments = studentCourseDetailsService.findByStudentAndCourseId(students.get(0).getId(), courseId).getAssignments();
			for(Assignment assignment : assignments) {
				int daysRemaining = findDayDifference(assignment);
				assignment.setDaysRemaining(daysRemaining);
				assignmentService.save(assignment);
			}
			if(assignments.size() == 0) {
				assignments = null;
			}
			List<GradeDetails> gradeDetailsList = new ArrayList<>();
			for(Student student : students) {
				gradeDetailsList.add(studentCourseDetailsService.findByStudentAndCourseId(student.getId(), courseId).getGradeDetails());
			}
			HashMap<List<Student>, List<GradeDetails>> studentGradeList = new HashMap<>();
			studentGradeList.put(students, gradeDetailsList);
			theModel.addAttribute("studentGradeList", studentGradeList);
			theModel.addAttribute("assignments", assignments);		
		} 
		
		theModel.addAttribute("teacher", teacher);
		theModel.addAttribute("course", course);
		theModel.addAttribute("courses", courses);
		theModel.addAttribute("students", students);
		
		return "teacher/teacher-course-details";
	}
	
	
	@GetMapping("/{teacherId}/courses/{courseId}/editGrades")
	public String editGradesForm(@PathVariable("teacherId") int teacherId, @PathVariable("courseId") int courseId, Model theModel) {
		Teacher teacher = teacherService.findByTeacherId(teacherId);
		Course course = courseService.findCourseById(courseId);
		List<Course> courses = teacher.getCourses();
		List<Student> students = course.getStudents();
		
		List<GradeDetails> gradeDetailsList = new ArrayList<>();
		for(Student student : students) {
			gradeDetailsList.add(studentCourseDetailsService.findByStudentAndCourseId(student.getId(), courseId).getGradeDetails());
		}
		
		HashMap<List<Student>, List<GradeDetails>> studentGradeList = new HashMap<>();
		studentGradeList.put(students, gradeDetailsList);
		
		theModel.addAttribute("studentGradeList", studentGradeList);
		theModel.addAttribute("course", course);
		theModel.addAttribute("courses", courses);
		theModel.addAttribute("teacher", teacher);
		theModel.addAttribute("students", students);
		theModel.addAttribute("gradeDetailsList", gradeDetailsList);
		
		return "teacher/edit-grades-form";
	}
	
	
	@PostMapping("/{teacherId}/courses/{courseId}/editGrades/save/{gradeDetailsId}")
	public String modifyGrades(@ModelAttribute GradeDetails gradeDetails, 
			@PathVariable("teacherId") int teacherId, @PathVariable("courseId") int courseId,
			@PathVariable("gradeDetailsId") int gradeDetailsId) throws Exception {
		
		Teacher teacher = teacherService.findByTeacherId(teacherId);
		Course course = courseService.findCourseById(courseId);
		//StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(gradeDetailsId, courseId);
		StudentCourseDetails studentCourseDetails = gradeDetailsService.findById(gradeDetailsId).getStudentCourseDetails();
		studentCourseDetails.setGradeDetails(gradeDetails);
		studentCourseDetailsService.save(studentCourseDetails);
		gradeDetailsService.deleteById(gradeDetailsId);
		//gradeDetailsService.save(gradeDetails);
		
	    return "redirect:/teacher/" + teacherId + "/courses/" + courseId;
	}
	

	private int findDayDifference(Assignment assignment) {
		String dateString = assignment.getDueDate();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		try {
			LocalDate dueDate = LocalDate.parse(dateString, dtf);
			LocalDate today = LocalDate.now();
			int dayDiff = (int) Duration.between(today.atStartOfDay(), dueDate.atStartOfDay()).toDays();
			
			return dayDiff;	
			
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		return -1;
	}
	
}




