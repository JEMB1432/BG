package jemb.bistrogurmand.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RadarChartCustom extends StackPane {
    private static final double MAX_RATING = 5.0;
    private static final int CHART_SIZE = 400;
    private static final double CENTER_X = CHART_SIZE / 2.0;
    private static final double CENTER_Y = CHART_SIZE / 2.0;
    private static final double MAX_RADIUS = 150;

    public RadarChartCustom(Map<String, Double> employeeRatings) {
        this.setPrefSize(CHART_SIZE, CHART_SIZE);
        this.setMaxSize(CHART_SIZE, CHART_SIZE);
        this.setPadding(new Insets(50));

        if (employeeRatings.isEmpty()) {
            Text noDataText = new Text("No hay datos para mostrar");
            noDataText.setFont(Font.font(16));
            noDataText.setFill(Color.GRAY);
            this.getChildren().add(noDataText);
            StackPane.setAlignment(noDataText, Pos.CENTER);
            return;
        }

        createRadarBackground();
        createDataPolygon(employeeRatings);
        addEmployeeLabels(employeeRatings);
        addScaleLabels();
    }

    private void createRadarBackground() {
        for (int i = 1; i <= MAX_RATING; i++) {
            double radius = (MAX_RADIUS * i) / MAX_RATING;
            Circle circle = new Circle(CENTER_X, CENTER_Y, radius);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.LIGHTGRAY);
            circle.setStrokeWidth(0.5);
            this.getChildren().add(circle);
        }
    }

    private void createDataPolygon(Map<String, Double> employeeRatings) {
        List<String> employeeNames = new ArrayList<>(employeeRatings.keySet());
        int numEmployees = employeeNames.size();

        if (numEmployees == 0) return;

        double angleOffset = (numEmployees == 2) ? Math.PI / 4 : Math.PI / 2;

        Polygon dataPolygon = new Polygon();

        for (int i = 0; i < numEmployees; i++) {
            double angle = 2 * Math.PI * i / numEmployees - angleOffset;

            double endX = CENTER_X + MAX_RADIUS * Math.cos(angle);
            double endY = CENTER_Y + MAX_RADIUS * Math.sin(angle);

            Line axisLine = new Line(CENTER_X, CENTER_Y, endX, endY);
            axisLine.setStroke(Color.web("#232323"));
            axisLine.setStrokeWidth(0.5);
            this.getChildren().add(axisLine);
        }

        for (int i = 0; i < numEmployees; i++) {
            String employee = employeeNames.get(i);
            Double rating = employeeRatings.get(employee);

            double angle = 2 * Math.PI * i / numEmployees - angleOffset;
            double radius = (MAX_RADIUS * rating) / MAX_RATING;

            double x = CENTER_X + radius * Math.cos(angle);
            double y = CENTER_Y + radius * Math.sin(angle);

            dataPolygon.getPoints().addAll(x, y);

            Circle dataPoint = new Circle(x, y, 3);
            dataPoint.setFill(Color.web("#990D35").deriveColor(0, 1, 1, 0.3));
            dataPoint.setStroke(Color.web("#990D35"));
            dataPoint.setStrokeWidth(1);

            // Tooltip con calificación
            Tooltip tooltip = new Tooltip(employee + ": " + String.format("%.2f", rating));
            Tooltip.install(dataPoint, tooltip);

            this.getChildren().add(dataPoint);
        }

        dataPolygon.setFill(Color.web("#990D35").deriveColor(0, 1, 1, 1));
        dataPolygon.setStroke(Color.web("#990D35"));
        dataPolygon.setStrokeWidth(2);

        this.getChildren().add(dataPolygon);
    }

    private void addEmployeeLabels(Map<String, Double> employeeRatings) {
        List<String> employeeNames = new ArrayList<>(employeeRatings.keySet());
        int numEmployees = employeeNames.size();
        double angleOffset = (numEmployees == 2) ? Math.PI / 4 : Math.PI / 2;
        double labelRadius = MAX_RADIUS + 30;

        for (int i = 0; i < numEmployees; i++) {
            String employee = employeeNames.get(i);
            Double rating = employeeRatings.get(employee);

            double angle = 2 * Math.PI * i / numEmployees - angleOffset;

            double labelX = CENTER_X + labelRadius * Math.cos(angle);
            double labelY = CENTER_Y + labelRadius * Math.sin(angle);

            Text label = new Text(String.format("%s\n(%.1f)", employee, rating));
            label.setFont(Font.font(10));
            label.setFill(Color.DARKSLATEGRAY);
            label.setTextAlignment(TextAlignment.CENTER);

            // Centrar relativo al centro del StackPane usando translate
            label.setTranslateX(labelX - CENTER_X);
            label.setTranslateY(labelY - CENTER_Y);

            this.getChildren().add(label);
        }
    }


    private void addScaleLabels() {
        for (int i = 1; i <= MAX_RATING; i++) {
            double radius = (MAX_RADIUS * i) / MAX_RATING;

            Text scaleLabel = new Text(String.valueOf(i));
            scaleLabel.setFont(Font.font(9));
            scaleLabel.setFill(Color.GRAY);
            scaleLabel.setLayoutX(CENTER_X + radius - 5);
            scaleLabel.setLayoutY(CENTER_Y - 5);

            this.getChildren().add(scaleLabel);
        }

        Text title = new Text("Calificación por Empleado");
        title.setFont(Font.font(13));
        title.setStyle("-fx-font-weight: bold");
        title.setFill(Color.web("#fff"));
        title.setLayoutX(CENTER_X);
        title.setLayoutY(10);
        this.getChildren().add(title);
    }
}
