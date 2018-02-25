package micropolisj.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import micropolisj.engine.MapState;
import micropolisj.engine.Micropolis;
import micropolisj.engine.map.MapPosition;
import micropolisj.engine.subway.SubwayConnection;
import micropolisj.engine.subway.SubwayNetwork;
import micropolisj.engine.subway.SubwayStation;

public class SubwayDialog extends JDialog {
	private JButton jbAddConnection;
	private JPanel panel;
	private JDialog dlg;
	private OverlayMapView mapView;
	private final JList<SubwayStation> stationList;
	private final JList<SubwayConnection> connList;
	private static ResourceBundle strings = ResourceBundle.getBundle("micropolisj.GuiStrings");

	public SubwayDialog(Window owner, Micropolis engine, SubwayNetwork subNet) {
		super(owner);
		dlg = this;
		panel = new JPanel(true);
		getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		mapView = new OverlayMapView(engine, 3);
		// mapView.setPreferredSize(preferredSize);
		mapView.setMapState(MapState.SUBWAY);
		mapView.setSize(new Dimension(600, 600));
		mapView.setPreferredSize(new Dimension(700, 700));
		// mapView.connectView(drawingArea, drawingAreaScroll);
		mapView.setSize(new Dimension(600, 600));
		panel.add(mapView, BorderLayout.CENTER);

		JPanel panelList = new JPanel(new FlowLayout(FlowLayout.CENTER), true);
		panel.add(panelList);

		JPanel panelStations = new JPanel(true);
		panelStations.setLayout(new BoxLayout(panelStations, BoxLayout.Y_AXIS));

		JPanel panelConnections = new JPanel(true);
		panelConnections.setLayout(new BoxLayout(panelConnections, BoxLayout.Y_AXIS));
		panelList.add(panelStations);
		panelList.add(panelConnections);

		JPanel panelDlgControl = new JPanel(new FlowLayout(FlowLayout.RIGHT), true);
		panel.add(panelDlgControl);

		JButton okBtn = new JButton(strings.getString("subDlg.btn.ok"));
		panel.add(okBtn);
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		Vector<SubwayStation> vStations = new Vector<SubwayStation>(engine.getSubways());
		stationList = new JList<SubwayStation>(vStations);
		panelStations.add(stationList);
		stationList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				List<MapPosition> posList = new ArrayList<>();
				for (SubwayStation subwayStation : stationList.getSelectedValuesList()) {
					posList.add(subwayStation.getPos());
				}
				mapView.setSelectedPosList(posList);
				mapView.repaint();
			}
		});
		jbAddConnection = new JButton(strings.getString("subDlg.btn.createConnection"));
		jbAddConnection.setEnabled(true);
		panelStations.add(jbAddConnection);
		jbAddConnection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (subNet.getSubConnectionCount() < 12) {
					if (stationList.getSelectedValuesList().size() == 2) {
						SubwayConnection newCon=subNet.connect(((SubwayStation) stationList.getSelectedValuesList().get(0)),
								((SubwayStation) stationList.getSelectedValuesList().get(1)));
						if (newCon!=null)
							((DefaultListModel<SubwayConnection>) connList.getModel()).addElement(newCon);
					}
				} else {
					JOptionPane.showMessageDialog(dlg, strings.getString("subDlg.msg.maxConnectionExceeded"));
				}
			}
		});

		DefaultListModel<SubwayConnection> conModel = new DefaultListModel<>();
		connList = new JList<>(conModel);
		for (SubwayConnection subwayConnection : subNet.getConnections()) {
			conModel.add(0, subwayConnection);
		}
		panelConnections.add(connList);
		connList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				mapView.setSelectedSubConnections(connList.getSelectedValuesList());
				mapView.repaint();
			}
		});
		jbAddConnection = new JButton(strings.getString("subDlg.btn.removeConnection"));
		jbAddConnection.setEnabled(true);
		panelConnections.add(jbAddConnection);
		jbAddConnection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<SubwayConnection> sels = connList.getSelectedValuesList();
				connList.setSelectedIndex(-1);
				for (SubwayConnection subwayConnection : sels) {
					subNet.removeConnection(subwayConnection);
					System.out.println("Loeschen: " + conModel.removeElement(subwayConnection));
				}
			}
		});

		pack();
		setSize(410, 500);
		setLocationRelativeTo(getParent());
		Color color = new Color(245, 181, 28);
		panel.setBackground(color);
	}
}
