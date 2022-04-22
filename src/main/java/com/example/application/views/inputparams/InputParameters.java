package com.example.application.views.inputparams;

import com.example.application.data.entity.SampleTest;
import com.example.application.data.service.SampleTestRepository;
import com.example.application.logic.BlockChain;
import com.example.application.logic.Miner;
import com.example.application.logic.User;
import com.example.application.logic.UserInterface;
import com.example.application.views.MainLayout;
import com.example.application.views.masterdetail.TestResultView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Security;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@PageTitle("Input Params")
@Route(value = "input_params", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class InputParameters extends HorizontalLayout {
    BlockChain blockChain;
    char[] chars;
    StringBuilder sb;
    Random random;
    char c;

    private NumberField numUsers;
    private NumberField numMiners;
    private NumberField difficulty;
    private NumberField duration;
    private UserInterface ui;
    private Button confirm;



    private SampleTest sampleTest;

    private final SampleTestRepository sampleTestService;


    private Paragraph avgMiningPower;
    private Paragraph numOfBlocks;
    private Paragraph numOfTransactions;
    private Paragraph numOfBlocksPerSec;
    private Paragraph numOfTransactionsPerSec;
    private Paragraph numOfTransactionsPerBlock;

    @Autowired
    public InputParameters(SampleTestRepository sampleTestService) {
        this.sampleTestService = sampleTestService;
        blockChain  = new BlockChain();
        ui = new UserInterface();
        chars = "abcdefghijklmnopqrstuvwxyz123456789".toCharArray();
        sb  = new StringBuilder(20);
        random = new Random();
        numUsers = new NumberField("Number of Users");
        numMiners = new NumberField ("Number of Miners");
        difficulty = new NumberField ("Hash difficulty");
        duration = new NumberField ("Duration (sec)");

        avgMiningPower = new Paragraph();
        numOfBlocks = new Paragraph();
        numOfTransactions = new Paragraph();
        numOfBlocksPerSec = new Paragraph();
        numOfTransactionsPerSec = new Paragraph();
        numOfTransactionsPerBlock = new Paragraph();

        confirm = new Button("Confirm");
        confirm.addClickListener(e -> {
            ui.numOfusers = numUsers.getValue();
            ui.numOfminers = numMiners.getValue();
            ui.hashDifficulty = difficulty.getValue();
            ui.runTime = duration.getValue();
            Notification.show(ui.numOfusers + " " + ui.numOfminers + " " + ui.hashDifficulty + " " + ui.runTime);
            play();
        });

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, numUsers, numMiners, difficulty, duration, confirm);
        add(numUsers, numMiners, difficulty, duration, confirm);
        setVerticalComponentAlignment(Alignment.END, avgMiningPower, numOfBlocks, numOfTransactions, numOfTransactionsPerSec, numOfTransactionsPerBlock);
        add(avgMiningPower, numOfBlocks, numOfTransactions, numOfTransactionsPerSec, numOfTransactionsPerBlock);
    }

    public List<User> createUsers(int numberOfUsers){
        ArrayList<User> users = new ArrayList<>();
        for(int i = 1; i<= numberOfUsers; i++){
            sb.setLength(0);
            for (int x = 1; x <= 5; x++) {
                c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }
            if(users.contains(sb.toString())){ continue;}
            users.add(new User(sb.toString(),blockChain));
        }
        return users;
    }
    public List<Miner> createMiners(int numberOfUsers){
        ArrayList<Miner> miners = new ArrayList<>();
        for(int i = 1; i<= numberOfUsers; i++){
            sb.setLength(0);
            for (int x = 1; x <= 5; x++) {
                c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }
            if(miners.contains(sb.toString())){ continue;}
            miners.add(new Miner(sb.toString(),blockChain));
        }
        return miners;
    }

    public BlockChain getBlockChain() {
        return blockChain;
    }

    public void play() {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        System.out.println("Enter number of users: ");
        ArrayList<User> users = (ArrayList<User>) createUsers(numUsers.getValue().intValue());
        System.out.println("Enter number of miners: ");
        ArrayList<Miner> miners = (ArrayList<Miner>) createMiners(numMiners.getValue().intValue());
        System.out.println("Enter difficulty: ");
        blockChain.setDifficulty(difficulty.getValue().intValue());
        System.out.println("Enter time of running in seconds: ");
        users.stream().forEach(p -> p.setUserList(users));
        for (User user : users) {
            user.start();
        }
        System.out.println("Started users");
        for (Miner miner : miners) {
            miner.start();
        }
        System.out.println("Started miners");
        try {
            Thread.sleep(duration.getValue().intValue() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        miners.stream().forEach(Thread::stop);
        users.stream().forEach(Thread::stop);
        int allMiningPower = 0;
        float minerCount = 0;
        for (Miner miner : miners) {
            allMiningPower += miner.getMinerHashPower();
            minerCount++;
        }
        DecimalFormat df = new DecimalFormat("#.###");
        System.out.println("****************************************************");


        if (this.sampleTest == null) {
            this.sampleTest = new SampleTest();
        }
        sampleTest.setAvgMiningPower(allMiningPower / minerCount);
        sampleTest.setNumOfBlocks(blockChain.getNumofBlocks());
        sampleTest.setNumOfTransactions(blockChain.getNumofTransactions());
        sampleTest.setNumOfBlocksPerSec(blockChain.getNumofBlocks() / duration.getValue().intValue());
        sampleTest.setNumOfTransactionsPerSec((double)blockChain.getNumofTransactions() / duration.getValue().intValue());
        sampleTest.setNumOfTransactionsPerBlock(blockChain.getNumofTransactions() / blockChain.getNumofBlocks());
        sampleTest.setNumberOfUsers(numUsers.getValue().intValue());
        sampleTest.setNumberOfMiners(numMiners.getValue().intValue());
        sampleTest.setHashDifficulty(difficulty.getValue().intValue());
        sampleTest.setDuration(duration.getValue().intValue());
        sampleTestService.save(this.sampleTest);


        Notification.show("Test details stored.");
        UI.getCurrent().navigate(TestResultView.class);


        avgMiningPower.setText(df.format(allMiningPower / minerCount));
        numOfBlocks.setText(blockChain.getNumofBlocks() + "");
        numOfTransactions.setText(blockChain.getNumofTransactions() + "");
        numOfBlocksPerSec.setText(df.format(blockChain.getNumofBlocks() / duration.getValue().intValue()));
        numOfTransactionsPerSec.setText(df.format(blockChain.getNumofTransactions() / duration.getValue().intValue()));
        numOfTransactionsPerBlock.setText(df.format(blockChain.getNumofTransactions() / blockChain.getNumofBlocks()));

        avgMiningPower.setVisible(true);
        numOfBlocks.setVisible(true);
        numOfTransactions.setVisible(true);
        numOfBlocksPerSec.setVisible(true);
        numOfTransactionsPerSec.setVisible(true);
        numOfTransactionsPerBlock.setVisible(true);
        System.out.println("Average mining power: " + df.format(allMiningPower / minerCount));
        System.out.println("Is the Blockchain valid: " + BlockChain.isChainValid(blockChain));
        System.out.println("Number of Blocks in Blockchain: " + (int) blockChain.getNumofBlocks());
        System.out.println("Number of Transactions in Blockchain: " + blockChain.getNumofTransactions());
        System.out.println("Number of Blocks/second: " + df.format(blockChain.getNumofBlocks() / duration.getValue().intValue()));
        System.out.println("Number of Transactions/second: " + df.format(blockChain.getNumofTransactions() / duration.getValue().intValue()));
        System.out.println("Number of Transactions/Block: " + df.format(blockChain.getNumofTransactions() / blockChain.getNumofBlocks()));
        System.out.println("****************************************************");
//        System.exit(0);
    }

}
