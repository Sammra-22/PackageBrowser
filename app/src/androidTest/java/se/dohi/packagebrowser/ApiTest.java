package se.dohi.packagebrowser;

import android.content.Context;
import android.test.InstrumentationTestCase;

import se.dohi.packagebrowser.api.GetPackageDetails;
import se.dohi.packagebrowser.api.GetPackages;
import se.dohi.packagebrowser.model.Bundle;
import se.dohi.packagebrowser.model.Environment;
import se.dohi.packagebrowser.model.Package;
import se.dohi.packagebrowser.network.ConnectionInfo;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApiTest extends InstrumentationTestCase{

    Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getTargetContext();
    }

    public void testGetPackages(){
        assertTrue("Not connected",ConnectionInfo.isConnected(mContext));
        new GetPackages(mContext).query();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse("Package list is empty", PackageManager.getInstance().getPackages().isEmpty());
        Package firstPckg = PackageManager.getInstance().getPackages().get(0);
        assertFalse("No languages found", firstPckg.getLanguages().isEmpty());

        new GetPackageDetails(mContext, firstPckg, null).query();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bundle result =PackageManager.getInstance().getBundle();
        assertNotNull("Bundle not received", result);
        assertNotNull("Bundle without info", result.getInfo());
        assertFalse("Bundle contains no Paths", result.getPaths().isEmpty());

        new GetPackageDetails(mContext, firstPckg, null).query(Environment.DEVELOPMENT, firstPckg.getLanguages().iterator().next());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bundle response =PackageManager.getInstance().getBundle();
        assertNotNull("Bundle not received", response);
        assertNotNull("Bundle without info", response.getInfo());
        assertFalse("Bundle contains no Paths", response.getPaths().isEmpty());


    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


}