package Project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;


public class LedgerDao {

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String id = "oraman";
	private String password = "oracle";
	

	public LedgerDao() {

		try { // �ؾ����� 5�� Ʈ���� ��ġ �ϱ� ���Թ� 1��
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	private Connection getConnection() {
		// �ؾ����� 7�� DriverManager.getConnection(url, id, password); �żҵ� �ȿ� �����
		// ���Թ� 2��
		// Ʈ���� ��ġ �ϱ�
		try {
			return DriverManager.getConnection(url, id, password);// ���Ͽ� �ֱ�
		} catch (SQLException e) {
			e.printStackTrace();
			return null; // return �����ؾ���
		}

	}

	// ���� �޼ҵ�
	public int DeleteData(int no) {

		String sql = "delete from ledger where no = ?";
		int cnt = -999999; // ������ �߰��� ����
		PreparedStatement pstmt = null;

		Connection conn = getConnection();

		try {

			conn.setAutoCommit(false);

			pstmt = conn.prepareStatement(sql); // Ʈ����� ó�� (������ �߰��� ����)

			pstmt.setInt(1, no); // ? ġȯ�ϴ� ��

			cnt = pstmt.executeUpdate(); // execute ���� ġȯ�ϴ� �� �������

			conn.commit(); // ���� �߿� ���� ������ (������ �߰��� ����)

		} catch (SQLException e) {

			try {
				conn.rollback(); // ������ �߰��� ����
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			// ���� ����� ���̳ʽ��� �ٿ��� ����
			// ȣ���ϴ� ������ ���� ���� ����� �̿��Ͽ� ������ ���� �޽����� ���� �ָ� �ȴ�.
			cnt = -e.getErrorCode();
			e.printStackTrace();

		} finally {

			try {

				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

		return cnt;

	}

	// ���� �޼ҵ�
	public int UpdateData(ListTable list) {

		//System.out.println( list.toString() );
		String sql = "update ledger set memo=?, pay=?, today=to_date(?,'yyyy/mm/dd'), price=?, balance=? where no= ?";

		int cnt = -999999; // ������ �߰��� ����
		PreparedStatement pstmt = null;

		Connection conn = getConnection();

		try {

			conn.setAutoCommit(false);

			pstmt = conn.prepareStatement(sql); // Ʈ����� ó�� (������ �߰��� ����)

			pstmt.setInt(6, list.getNo()); // ? ġȯ�ϴ� ��
			pstmt.setString(1, list.getMemo());
			pstmt.setInt(2, list.getPay());
			pstmt.setString(3, list.getToday());
			pstmt.setInt(4, list.getPrice());
			pstmt.setInt(5, list.getBalance());

			cnt = pstmt.executeUpdate(); // execute ���� ġȯ�ϴ� �� �������

			conn.commit(); // ���� �߿� ���� ������ (������ �߰��� ����)

		} catch (SQLException e) {

			try {
				conn.rollback(); // ������ �߰��� ����
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			// ���� ����� ���̳ʽ��� �ٿ��� ����
			// ȣ���ϴ� ������ ���� ���� ����� �̿��Ͽ� ������ ���� �޽����� ���� �ָ� �ȴ�.
			cnt = -e.getErrorCode();
			e.printStackTrace();

		} finally {

			try {

				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

		return cnt;

	}

	// ���� �޼ҵ�

	public int InsertData(ListTable list) {

		String sql = "insert into ledger (no, today, pay, memo, price, balance)";
		sql += " values(myseq.nextval,to_date(?, 'yyyy/mm/dd'),?,?,?,?)";

		int cnt = -999999; // ������ �߰��� ����
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;

		ResultSet rs = null;
		Connection conn = getConnection();

		try {
			String sql2 = " select balance from ledger ";
			sql2 += " where no = ( select max(no) from ledger)";
			pstmt2 = conn.prepareStatement(sql2);
			rs = pstmt2.executeQuery();
			int balance = 0;
			if (rs.next()) {
				//System.out.println("balance");
				balance = rs.getInt("balance");
			}else{
				balance = 0;
			}

			conn.setAutoCommit(false);

			pstmt = conn.prepareStatement(sql); // Ʈ����� ó�� (������ �߰��� ����)

			// pstmt.setInt(1, list.getNo()); // ? ġȯ�ϴ� ��
			pstmt.setString(1, list.getToday());
			pstmt.setInt(2, list.getPay());
			pstmt.setString(3, list.getMemo());
			pstmt.setInt(4, list.getPrice());
			
			// �ܾ��� ���� ���� �ܾװ� ��� ����
			if (list.getPay() == 1) {
				balance += list.getPrice();
			} else {
				balance -= list.getPrice();
			}
			pstmt.setInt(5, balance);

			cnt = pstmt.executeUpdate(); // execute ���� ġȯ�ϴ� �� �������

			conn.commit(); // ���� �߿� ���� ������ (������ �߰��� ����)

		} catch (SQLException e) {

			try {
				conn.rollback(); // ������ �߰��� ����
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			// ���� ����� ���̳ʽ��� �ٿ��� ����
			// ȣ���ϴ� ������ ���� ���� ����� �̿��Ͽ� ������ ���� �޽����� ���� �ָ� �ȴ�.
			cnt = -e.getErrorCode();
			e.printStackTrace();

		} finally {

			try {

				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

		return cnt;

	}

	// ��ü �ҷ�����
	public List<ListTable> GetLedgerList() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from ledger "; // �����ݷ� ������ ����
		Connection conn = getConnection();

		List<ListTable> lists = new ArrayList<ListTable>();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ListTable oneitem = new ListTable();
				oneitem.setNo(rs.getInt("no"));
				oneitem.setToday( String.valueOf( rs.getDate("today")));
				oneitem.setPay(  rs.getInt("pay") );
				oneitem.setMemo( rs.getString("memo") );
				oneitem.setPrice( rs.getInt("price") ); 
				oneitem.setBalance(rs.getInt("balance"));
 

				lists.add(oneitem);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return lists;
	}
	
	//�����Ѵ� ��ȸ	
	public List<ListTable> GetListMonth( int year, int month  ){ //���� �ڵ��Ѱ� �����ϱ�

		PreparedStatement pstmt = null ;
		
		ResultSet rs = null ; 
		
		String sql = "select * from ledger where today between to_date(?,'yyyy/MM/dd') and to_date(?,'yyyy/MM/dd')" ; 	//�ؾ����� 15 ""���� ���� �ٲ��ֱ�
		Connection conn = getConnection() ;		
		
		
		Utility util = new Utility(year, month) ;
		String xx = util.getFirstDay() ;
		String yy = util.getLastDay() ;
		
		List<ListTable> lists = new ArrayList<ListTable>(); // �ؾ����� 14 	
		
		
		try {
			
			pstmt = conn.prepareStatement(sql) ;
			
			pstmt.setString(1, xx); // �ؾ����� 18  ?�� �ٲٱ� ���� ���� {1�� �ǹ̴� ?�� ��ġ}
			pstmt.setString(2, yy);
			//System.out.println( xx + "," + yy );
			
			rs = pstmt.executeQuery() ; 
			
			
			while( rs.next() ){ 
				ListTable oneitem = new ListTable(); 
				oneitem.setNo(rs.getInt("no"));
				oneitem.setToday( String.valueOf( rs.getDate("today")));
				oneitem.setPay(  rs.getInt("pay") );
				oneitem.setMemo( rs.getString("memo") );
				oneitem.setPrice( rs.getInt("price") ); 
				oneitem.setBalance(rs.getInt("balance"));
				lists.add(oneitem);
			}
			
			
		} catch (SQLException e) {			
			e.printStackTrace();
		}finally{
			try {
				
				if( rs != null ){rs.close();}
				if( pstmt != null ){pstmt.close();}
				if( conn != null ){conn.close();}
				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}	
		return lists ;
	}
	
	
	
	
	//�̹��� ���� ��ȸ	
		public List<ListTable> GetListNowMonth(int nowyear, int nowmonth ){ 
			
			
			PreparedStatement pstmt = null ;
			
			ResultSet rs = null ; 
			
			String sql = "select pay, sum(price) as sumprice from ledger where today between to_date(?,'yyyy/MM/dd') and to_date(?,'yyyy/MM/dd') group by pay " ; 	//�ؾ����� 15 ""���� ���� �ٲ��ֱ�
			Connection conn = getConnection() ;		
			
			
			Utility util = new Utility(nowyear, nowmonth) ;
			String xx = util.getFirstDay() ;
			String yy = util.getLastDay() ;
			
			List<ListTable> lists = new ArrayList<ListTable>(); // �ؾ����� 14 	
			
			ListTable oneitem = null;
			
			try {
				
				pstmt = conn.prepareStatement(sql) ;
				
				pstmt.setString(1, xx); // �ؾ����� 18  ?�� �ٲٱ� ���� ���� {1�� �ǹ̴� ?�� ��ġ}
				pstmt.setString(2, yy);
				
				rs = pstmt.executeQuery() ; 
				
				
				while( rs.next() ){ 
					//ListTable oneitem = new ListTable(); 
					
					oneitem = new ListTable();
					oneitem.setPay(  rs.getInt("pay") );
					oneitem.setPrice(rs.getInt("sumprice"));
					lists.add(oneitem);
					
				}
				
				
			} catch (SQLException e) {			
				e.printStackTrace();
			}finally{
				try {
					
					if( rs != null ){rs.close();}
					if( pstmt != null ){pstmt.close();}
					if( conn != null ){conn.close();}
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}	
			return lists ;
		}
		
		//�̹��� ���� 
				public List<ListTable> GetListNowMonthSum(int nowyear, int nowmonth ,int pay ){ 
					
					
					PreparedStatement pstmt = null ;
					
					ResultSet rs = null ; 
					
					String sql = "select pay, sum(price) as sumprice from ledger ";
					sql+= " where (today between to_date(?,'yyyy/MM/dd') and to_date(?,'yyyy/MM/dd')) and  pay=? ";
					sql+= " group by pay " ; 	//�ؾ����� 15 ""���� ���� �ٲ��ֱ�
					Connection conn = getConnection() ;		
					
					
					Utility util = new Utility(nowyear, nowmonth) ;
					String xx = util.getFirstDay() ;
					String yy = util.getLastDay() ;
					
					List<ListTable> lists = new ArrayList<ListTable>(); // �ؾ����� 14 	
					
					ListTable oneitem = null;
					
					
					try {
						
						pstmt = conn.prepareStatement(sql) ;
						
						pstmt.setString(1, xx); // �ؾ����� 18  ?�� �ٲٱ� ���� ���� {1�� �ǹ̴� ?�� ��ġ}
						pstmt.setString(2, yy);
						pstmt.setInt(3, pay);
						
						rs = pstmt.executeQuery() ; 
						
						
						while( rs.next() ){ 
							//ListTable oneitem = new ListTable(); 
							
							oneitem = new ListTable();
							oneitem.setPay(  rs.getInt("pay") );
							oneitem.setPrice(rs.getInt("sumprice"));
							lists.add(oneitem);
							
							
						}
						
						
					} catch (SQLException e) {			
						e.printStackTrace();
					}finally{
						try {
							
							if( rs != null ){rs.close();}
							if( pstmt != null ){pstmt.close();}
							if( conn != null ){conn.close();}
							
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}	
					return lists ;
				}
	
	
}
