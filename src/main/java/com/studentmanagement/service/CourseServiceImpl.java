package com.studentmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentmanagement.dao.CourseDao;
import com.studentmanagement.entity.Course;

@Service
public class CourseServiceImpl implements CourseService {
	
	@Autowired
	private CourseDao courseDao;
	
	@Override
	@Transactional
	public void save(Course course) {
		courseDao.saveCourse(course);
	}


	@Override
	@Transactional
	public Course findCourseById(int id) {
		return courseDao.findCourseById(id);
	}

	@Override
	@Transactional
	public void deleteCourseById(int id) {
		courseDao.deleteCourseById(id);		
	}

}
