package org.edforge.efhomescreen;


public interface IEdForgeLauncher {

    public boolean queryUserExists();
    public void gotoHome();
    public void startBreakOut();

    public void nextStep(String stepID);
}
