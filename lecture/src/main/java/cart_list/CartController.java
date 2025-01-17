package cart_list;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.Classes;
import Model.Course;
import Model.Department;
import lecture_list.ClassDAO;
import lecture_list.CourseDAO;
import lecture_list.DepartmentDAO;
import lecture_list.EnrollmentDAO;

@WebServlet("/cart")
public class CartController extends HttpServlet {
	
	private CourseDAO courseDAO;
	private ClassDAO classDAO;
	private DepartmentDAO departmentDAO;
	private CartDAO cartDAO;
	private EnrollmentDAO enrollmentDAO;
	
	@Override
	public void init() throws ServletException {
		
		courseDAO = new CourseDAO();
		classDAO = new ClassDAO();
		departmentDAO = new DepartmentDAO();
		cartDAO = new CartDAO();
		enrollmentDAO = new EnrollmentDAO();
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		if(session == null || session.getAttribute("student_id") == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		
		int studentId = (int)session.getAttribute("student_id");
		
		List<Course> courses = courseDAO.getAllCourses();
		req.setAttribute("allCourses", courses);
		
		Map<Integer, Integer> cartList = cartDAO.getCartList(studentId);
		req.setAttribute("cartList", cartList);
		
        // 학생이 이미 신청한 강의의 course_id와 class_id 목록 조회
        Map<Integer, Integer> enrolledCourses = enrollmentDAO.getEnrolledCourses(studentId);
        req.setAttribute("enrolledCourses", enrolledCourses);
		
		List<Classes> allClasses = classDAO.getAllClasses(studentId);
		req.setAttribute("allClasses", allClasses);
		
		List<Department> departments = departmentDAO.getAllDepartments();
		Map<Integer, String> departmentsMap = new HashMap<>();
		for(Department dept : departments) {
			departmentsMap.put(dept.getDepartmentId(), dept.getDepartmentName());
		}
		req.setAttribute("departmentsMap", departmentsMap);
		
		int currentCredits = cartDAO.getCurrentCredits(studentId);
		System.out.println("CartController - Current Credits: " + currentCredits);
		req.setAttribute("currentCredits", currentCredits);
		
		int enrolledCredits = enrollmentDAO.getCurrentCredits(studentId);
		req.setAttribute("enrolledCredits", enrolledCredits);
		
		req.getRequestDispatcher("WEB-INF/views/cart/cartList.jsp").forward(req, resp);
		
	}

}
