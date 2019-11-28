package views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import domain.UserVO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class LoginController extends MasterController {
	@FXML
	private TextField txtId;
	@FXML
	private PasswordField passField;

	@FXML
	private AnchorPane loginPane;
	
	@Override
	public void reset() {
		txtId.setText("");
		passField.setText("");
	}

	public void loginProcess() {
		String id = txtId.getText();
		String pass = passField.getText();

		if (id.isEmpty() || pass.isEmpty()) {
			Util.showAlert("에러", "아이디나 비밀번호는 공백일 수 없습니다.", AlertType.ERROR);
			return;
		}

		UserVO user = checkLogin(id, pass);

		if (user != null) {	
			MainController mc = (MainController)MainApp.app.getController("main");
			mc.setLoginInfo(user);
			MainApp.app.slideOut(loginPane);
		} else {
			Util.showAlert("알림", "존재하지 않는 아이디이거나 비밀번호가 틀립니다.", AlertType.INFORMATION);
			return;
		}
	}

	private UserVO checkLogin(String id, String pw) {

		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM project_users WHERE id = ? AND pass = PASSWORD(?)";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				UserVO user = new UserVO();
				user.setId(rs.getString("id"));
				user.setPass(rs.getString("pass"));
				user.setName(rs.getString("name"));
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Util.showAlert("error", "데이터베이스 처리중 오류 발생. 인터넷 확인 필요", AlertType.ERROR);		
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
		return null;	
	}
	public void registerPage() {
		MainApp.app.loadPage("register");
		
	}
}
