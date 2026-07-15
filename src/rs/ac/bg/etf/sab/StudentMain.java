package rs.ac.bg.etf.sab;

import rs.ac.bg.etf.sab.operations.*;
import rs.ac.bg.etf.sab.tests.TestHandler;
import rs.ac.bg.etf.sab.tests.TestRunner;
import student.ss220011_Impl;

public class StudentMain {
    public static void main(String[] args) throws Exception {
        
        ss220011_Impl impl = new ss220011_Impl();
        
       TestHandler.createInstance(
                impl,  // genresOperations
                impl,  // moviesOperations
                impl,  // ratingsOperation
                impl,  // tagsOperations
                impl,  // usersOperations
                impl,  // watchlistsOperations
                impl); // generalOperations
        
        TestRunner.runTests();
    }
}


