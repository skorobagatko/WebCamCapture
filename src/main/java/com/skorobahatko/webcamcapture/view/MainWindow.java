package com.skorobahatko.webcamcapture.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.skorobahatko.webcamcapture.controller.MainController;
import com.skorobahatko.webcamcapture.exception.AudioDeviceException;
import com.skorobahatko.webcamcapture.exception.NoCamerasFoundException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MainWindow extends JFrame {

    private static final int DEFAULT_WINDOW_WIDTH = 640;
    private static final int DEFAULT_WINDOW_HEIGHT = 480;

    private WebcamPanel webcamPanel;
    private JButton recordButton;
    private JComboBox<String> cameraChooser;

    private MainController mainController;


    public MainWindow() throws HeadlessException {
        init();
    }

    private void init() {
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new OnCloseWindowListener());
        setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);


        try {
            initIcon();

            initMainController();

            initRecordButton();

            initCameraChooser();

            initWebcamPanel();

            add(webcamPanel);

        } catch (NoCamerasFoundException e) {
            JLabel errorLabel = new JLabel();
            errorLabel.setText("No cameras found");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setVerticalAlignment(SwingConstants.CENTER);
            add(errorLabel);
        } catch (AudioDeviceException e) {
            showAudioWarningDialog(e.getMessage());
        }

        setVisible(true);
    }

    private void initIcon() {
        try {
            URI uri = getClass().getResource("/icon.png").toURI();
            byte[] bytes = Files.readAllBytes(Paths.get(uri));
            Image icon = new ImageIcon(bytes).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            /*NOP*/
        }
    }

    private void showAudioWarningDialog(String message) {
        JLabel label = new JLabel();
        label.setText(message);
        JDialog dialog = new JDialog();
        dialog.add(label);
        dialog.setLocationRelativeTo(null);
        dialog.setTitle("Warning!");
        dialog.setSize(100, 100);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    private void initMainController() throws NoCamerasFoundException, AudioDeviceException {
        mainController = new MainController();
    }

    private void initWebcamPanel() {
        webcamPanel = new WebcamPanel(mainController.getCurrentCamera(), null, true);
        webcamPanel.setImageSizeDisplayed(true);
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setMirrored(true);

        webcamPanel.add(recordButton);
        webcamPanel.add(cameraChooser);
    }

    private void initRecordButton() {
        recordButton = new JButton();
        recordButton.setText("Start Record");

        ActionListener listener = e -> {
            JButton button = (JButton) e.getSource();
            ButtonModel model = button.getModel();
            if (!model.isSelected()) {
                model.setSelected(true);
                model.setPressed(true);
                mainController.startRecord();
                button.setText("Stop Record");
            } else {
                model.setSelected(false);
                mainController.stopRecord();
                button.setText("Start Record");
            }
        };

        recordButton.addActionListener(listener);

        // TODO Add key map for start/stop recording
    }

    private void initCameraChooser() {
        cameraChooser = new JComboBox<>();

        // add all founded cameras to the checkbox
        List<Webcam> cameras = mainController.getCameras();
//        for (int i = 0; i < cameras.size(); i++) {
//            cameraChooser.addItem("Camera " + i);
//        }
        for (Webcam camera : cameras) {
            cameraChooser.addItem(camera.getName());
        }

        cameraChooser.addActionListener(e -> {
            int selectedIndex = cameraChooser.getSelectedIndex();

            // destroy webcamPanel for previous selected camera
            webcamPanel.stop();
            webcamPanel.setVisible(false);
            remove(webcamPanel);

            // set selected camera as current camera
            mainController.chooseCamera(selectedIndex);

            // re-initialize webcamPanel with new selected camera
            initWebcamPanel();
            add(webcamPanel);
            webcamPanel.setVisible(true);
            webcamPanel.start();
        });
    }

    private class OnCloseWindowListener extends WindowAdapter {
        // Prepare program to close by closing webcam panel with current webcam
        @Override
        public void windowClosing(WindowEvent e) {
            if (webcamPanel != null) {
                webcamPanel.stop();
            }
        }
    }
}
