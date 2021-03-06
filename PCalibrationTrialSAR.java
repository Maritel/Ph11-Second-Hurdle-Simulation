package mainpackage;

import java.util.Random;

public class PCalibrationTrialSAR {

	// runs exactly like PCalibrationTrialNormal except that the school can't
	// close, everyone is unvaccinated, and all students are always present

	private final double HOMEROOM_TIME_RATIO = 0.774;
	private final int CLASS_SIZE = 25;
	private final int CLASSES = 20;
	private final int THRESHOLD = 10;
	private final int CLOSING_DAYS = 5;

	private PStudent[] students;
	private Random rng;

	private double S;
	private double I;

	public PCalibrationTrialSAR(double S, double I) {
		rng = new Random();

		this.S = S;
		this.I = I;

		students = new PStudent[CLASS_SIZE * CLASSES];
		for (int i = 0; i < students.length; i++) {
			students[i] = new PStudent(false, rng);
		}
	}

	public int runYear() {
		for (int i = 1; i <= 365; i++) {
			runDay(i);
		}

		int counter = 0;
		for (PStudent s : students) {
			if (s.wasInfected()) {
				counter++;
			}
		}
		return counter;
	}

	public void runDay(int day) {
		attemptExternalInfection();
		if (isSchoolOpen(day)) {
			spreadDiseaseInSchool();
		}
		advanceAllStudents();
	}

	private void attemptExternalInfection() {
		for (PStudent s : students) {
			if (Math.random() < S)
				s.attemptToInfect();
		}
	}

	private boolean isSchoolOpen(int i) {
		if (i % 7 == 4 || i % 7 == 5)
			return false; // weekends
		else if (i < 86)
			return false; // school hasn't started yet
		else if (i >= 165 && i <= 171)
			return false; // Thanksgiving break
		else if (i >= 186 && i <= 199)
			return false; // Christmas break
		else if (i >= 298 && i <= 304)
			return false; // Spring break
		else
			return true;

	}

	private void spreadDiseaseInSchool() {
		for (int i = 0; i < students.length; i++) {
			PStudent thisStudent = students[i];
			if (thisStudent.isSick()) {
				double counter = I;
				while (counter > 1) {
					attemptToInfectSomeoneElse(i);
					counter -= 1;
				}

				if (Math.random() < counter) {
					attemptToInfectSomeoneElse(i);
				}
			}
		}
	}

	// i is an array index
	private void attemptToInfectSomeoneElse(int i) {
		int classNumber = i / 25;
		if (Math.random() < HOMEROOM_TIME_RATIO) {
			int studentNumber = i % 25;
			int other = rng.nextInt(24);
			if (other >= studentNumber)
				other++;
			PStudent otherStudent = students[25 * classNumber + other];
			otherStudent.attemptToInfect();
		} else {
			int otherClass = rng.nextInt(CLASSES - 1);
			if (otherClass >= classNumber)
				otherClass++;
			PStudent otherStudent = students[25 * otherClass + rng.nextInt(25)];
			otherStudent.attemptToInfect();
		}
	}

	private void advanceAllStudents() {
		for (PStudent s : students)
			s.advanceDay();
	}
}
