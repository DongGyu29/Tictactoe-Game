package views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class RegisterController extends MasterController {
	@FXML
	private TextField txtI;
	@FXML
	private TextField txtName;
	@FXML
	private PasswordField pass;
	@FXML
	private PasswordField passConfirm;
	@FXML
	private TextArea txtInfo;
	@FXML
	private AnchorPane registerPane;
	@Override
	public void reset() {
		
		txtI.setText("");
		txtName.setText("");
		pass.setText("");
		passConfirm.setText("");
	}

	public void register() {
		String id = txtI.getText().trim();
		String name = txtName.getText().trim();
		String strPass = pass.getText().trim();
		String confirm = passConfirm.getText().trim();	

		if (id.isEmpty() || name.isEmpty() || strPass.isEmpty()) {
			Util.showAlert("비어있음", "필수 값이 비어있습니다.", AlertType.INFORMATION);
			return;
		}
		if (!strPass.equals(confirm)) {
			Util.showAlert("불일치", "비밀번호와 확인이 다릅니다.", AlertType.INFORMATION);
			return;
		}
		
		
		Connection con = JDBCUtil.getConnection(); 
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlExist = "SELECT * FROM project_users WHERE id = ?";
		String sqlInsert = "INSERT INTO project_users(id, name, pass)"
				+ " VALUES (?, ?, PASSWORD(?))";

		try {
			pstmt = con.prepareStatement(sqlExist);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Util.showAlert("중복", "이미 해당 ID의 유저가 존재", AlertType.INFORMATION);
				return;
			}

			JDBCUtil.close(pstmt);

			pstmt = con.prepareStatement(sqlInsert);
			pstmt.setString(1, id);
			pstmt.setString(2, name);
			pstmt.setString(3, strPass);

			if (pstmt.executeUpdate() != 1) {
				Util.showAlert("에러", "데이터베이스 입력 중 오류발생", AlertType.ERROR);
				return;
			}

			Util.showAlert("성공", "성공적으로 회원가입되었습니다.", AlertType.INFORMATION);
			MainApp.app.slideOut(getRoot());

		} catch (Exception e) {
			e.printStackTrace();
			Util.showAlert("에러", "데이터베이스 입력 중 오류발생", AlertType.ERROR);
			return;
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}
	

	public void cancel() {
		MainApp.app.slideOut(getRoot()); 		
		
	}	
}
