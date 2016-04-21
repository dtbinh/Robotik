public abstract class State {
    private LineFollower context;

    public State(LineFollower context) {
        this.context = context;
    }

    public abstract void enter();
    public abstract void leave();
}
