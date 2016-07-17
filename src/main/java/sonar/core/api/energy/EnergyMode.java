package sonar.core.api.energy;

public enum EnergyMode {
	RECIEVE, SEND, SEND_RECIEVE, BLOCKED;

	public boolean canSend() {
		return this == SEND || this == SEND_RECIEVE;
	}

	public boolean canRecieve() {
		return this == RECIEVE || this == SEND_RECIEVE;
	}

	public boolean canConnect() {
		return this != BLOCKED;
	}
}