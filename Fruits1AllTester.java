import static java.lang.System.currentTimeMillis;

public class Fruits1AllTester {
    public static void main(String[] args) throws Exception {
        double start = currentTimeMillis();

        ProjectTester tester = new Model(); //Instantiate your own ProjectTester instance here
        tester.initialize();
        tester.crawl("https://people.scs.carleton.ca/~davidmckenney/fruits/N-0.html");
//
//        Fruits1OutgoingLinksTester.runTest(tester);
//        Fruits1IncomingLinksTester.runTest(tester);
//        Fruits1PageRanksTester.runTest(tester);
//        Fruits1IDFTester.runTest(tester);
//        Fruits1TFTester.runTest(tester);
//        Fruits1TFIDFTester.runTest(tester);
        Fruits1SearchTester.runTest(tester);
        System.out.println("Total time: " + (currentTimeMillis() - start));
        System.out.println("Finished running all tests.");
    }
}
