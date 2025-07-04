package HospitalManagementSystem;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="Hbeast007";
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner=new Scanner(System.in);
        try{
            Connection connection= DriverManager.getConnection(url,username,password);
            Patient patient=new Patient(connection,scanner);
            Doctor doctor=new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1.Add Patients");
                System.out.println("2.View Patients");
                System.out.println("3.View Doctors");
                System.out.println("4.Book Appointment");
                System.out.println("5.Exit");
                System.out.println("Enter Your choice");
                int choice=scanner.nextInt();

                switch(choice){
                    case 1:
                        //   Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
//                        view patients
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("Thank You for using Hospital Management System!!!");
                        return;
                    default:
                    System.out.println("Enter valid choice!!");

                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection , Scanner scanner){
        System.out.print("Enter Patient ID:");
        int patientID=scanner.nextInt();
        System.out.print("Enter Doctor ID:");
        int doctorID=scanner.nextInt();
        System.out.print("Enter Appointment Date (YYYY-MM-DD):");
        String appointmentDate=scanner.next();
        if(patient.getPatientById(patientID) && doctor.getDoctorById(doctorID)){
            if(checkDoctorAvailibility(doctorID,appointmentDate,connection)){
            String appointmentQuery="INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";
            try{
                PreparedStatement preparedStatement=connection.prepareStatement(appointmentQuery);
                preparedStatement.setInt(1,patientID);
                preparedStatement.setInt(2,doctorID);
                preparedStatement.setString(3,appointmentDate);
                int rowsAffected=preparedStatement.executeUpdate();
                if(rowsAffected>0){
                    System.out.println("Appointment Booked!!");
                }
                else{
                    System.out.println("Failed to book Appointment");
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
            }
            else{
                System.out.println("Doctor not available on this date");
            }
        }
        else{
            System.out.println("Either patient or doctor doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvailibility(int doctorID,String appointmentDate,Connection connection){
        String query="SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=?";
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorID);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                int count=resultSet.getInt(1);
                if(count==0) return true;
                else return false;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}

