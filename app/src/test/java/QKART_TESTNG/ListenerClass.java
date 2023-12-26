package QKART_TESTNG;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass implements ITestListener {

     public void onTestStart(ITestResult result) {
        System.out.println("New Test Started" +result.getName() + "Taking Screenshot!");
    }

    public void onTestSuccess(ITestResult result) {
        System.out.println("onTestSuccess Method" +result.getName() + "Taking Screenshot!");
    }

    public void onTestFailure(ITestResult result) {
        System.out.println("Test Failed : " + result.getName() + " Taking Screenshot !");
    }
}

