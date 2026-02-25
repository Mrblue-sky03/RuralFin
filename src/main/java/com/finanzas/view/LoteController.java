package com.finanzas.view;

import com.finanzas.MainApp;
import com.finanzas.model.Lote;
import com.finanzas.model.Movimiento;
import com.finanzas.model.RegistroPoblacion;
import com.finanzas.model.TipoMovimiento;
import com.finanzas.service.MovimientoService;
import com.finanzas.service.RegistroPoblacionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class LoteController {

    // Población
    @FXML private Label lblPoblacionInicial;
    @FXML private TextField txtPoblacionInicial;
    @FXML private Button btnGuardarPoblacion;
    @FXML private TableView<RegistroPoblacion> tablaRegistros;
    @FXML private TableColumn<RegistroPoblacion, String> colRegFecha;
    @FXML private TableColumn<RegistroPoblacion, String> colRegEnfermos;
    @FXML private TableColumn<RegistroPoblacion, String> colRegHeridos;
    @FXML private TableColumn<RegistroPoblacion, String> colRegMuertos;
    @FXML private TextField txtEnfermos;
    @FXML private TextField txtHeridos;
    @FXML private TextField txtMuertos;
    @FXML private DatePicker dpFechaRegistro;
    @FXML private Label lblPoblacionFinal;
    @FXML private Label lblTotalMuertos;

    // Movimientos
    @FXML private Label lblTitulo;
    @FXML private TableView<Movimiento> tablaMovimientos;
    @FXML private TableColumn<Movimiento, String> colFecha;
    @FXML private TableColumn<Movimiento, String> colNombre;
    @FXML private TableColumn<Movimiento, String> colTipo;
    @FXML private TableColumn<Movimiento, String> colMonto;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<TipoMovimiento> cmbTipo;
    @FXML private TextField txtMonto;
    @FXML private DatePicker dpFecha;
    @FXML private Label lblIngresos;
    @FXML private Label lblGastos;
    @FXML private Label lblBalance;

    private final MovimientoService service;
    private final RegistroPoblacionService poblacionService;
    private final ApplicationContext context;
    private Lote lote;

    public LoteController(MovimientoService service, RegistroPoblacionService poblacionService, ApplicationContext context) {
        this.service = service;
        this.poblacionService = poblacionService;
        this.context = context;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
        lblTitulo.setText("Lote: " + lote.getNombre());
        actualizarPoblacion();
        cargarRegistrosPoblacion();
        cargarMovimientos();
    }

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(TipoMovimiento.values()));

        colRegFecha.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFecha().toString()));
        colRegEnfermos.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getEnfermos())));
        colRegHeridos.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getHeridos())));
        colRegMuertos.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getMuertos())));

        colFecha.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFecha().toLocalDate().toString()));
        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colTipo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTipo().name()));
        colMonto.setCellValueFactory(data -> {
            Movimiento m = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(formatearMonto(m.getMonto(), m.getTipo()));
        });

        colMonto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTextFill(Color.BLACK);
                } else {
                    setText(item);
                    setTextFill(item.startsWith("-") ? Color.RED : Color.GREEN);
                }
            }
        });
    }

    private void actualizarPoblacion() {
        if (lote.getPoblacionInicial() != null) {
            lblPoblacionInicial.setText("Población inicial: " + lote.getPoblacionInicial());
            txtPoblacionInicial.setVisible(false);
            btnGuardarPoblacion.setVisible(false);
        } else {
            lblPoblacionInicial.setText("Población inicial: no registrada");
            txtPoblacionInicial.setVisible(true);
            btnGuardarPoblacion.setVisible(true);
        }
    }

    @FXML
    private void guardarPoblacionInicial() {
        try {
            Long poblacion = Long.parseLong(txtPoblacionInicial.getText().trim());
            poblacionService.guardarPoblacionInicial(lote, poblacion);
            actualizarPoblacion();
            actualizarResumenPoblacion();
        } catch (NumberFormatException e) {
            mostrarError("La población debe ser un número entero válido.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cargarRegistrosPoblacion() {
        List<RegistroPoblacion> lista = poblacionService.listarPorLote(lote);
        tablaRegistros.setItems(FXCollections.observableArrayList(lista));
        actualizarResumenPoblacion();
    }

    private void actualizarResumenPoblacion() {
        lblTotalMuertos.setText("Total muertos: " + poblacionService.totalMuertos(lote));
        lblPoblacionFinal.setText("Población final: " + poblacionService.poblacionFinal(lote));
    }

    @FXML
    private void guardarRegistroPoblacion() {
        try {
            LocalDate fecha = dpFechaRegistro.getValue();
            if (fecha == null) fecha = LocalDate.now();
            Long enfermos = txtEnfermos.getText().trim().isEmpty() ? 0L : Long.parseLong(txtEnfermos.getText().trim());
            Long heridos = txtHeridos.getText().trim().isEmpty() ? 0L : Long.parseLong(txtHeridos.getText().trim());
            Long muertos = txtMuertos.getText().trim().isEmpty() ? 0L : Long.parseLong(txtMuertos.getText().trim());
            poblacionService.guardar(fecha, enfermos, heridos, muertos, lote);
            txtEnfermos.clear();
            txtHeridos.clear();
            txtMuertos.clear();
            dpFechaRegistro.setValue(null);
            cargarRegistrosPoblacion();
        } catch (NumberFormatException e) {
            mostrarError("Los valores deben ser números enteros válidos.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarRegistroPoblacion() {
        RegistroPoblacion seleccionado = tablaRegistros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un registro para eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar este registro?");
        confirmacion.setContentText("Fecha: " + seleccionado.getFecha());
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            poblacionService.eliminar(seleccionado.getId());
            cargarRegistrosPoblacion();
        }
    }

    private void cargarMovimientos() {
        List<Movimiento> lista = service.listarPorLote(lote);
        tablaMovimientos.setItems(FXCollections.observableArrayList(lista));
        actualizarTotales();
    }

    private void actualizarTotales() {
        lblIngresos.setText(formatearMonto(service.totalIngresos(lote), TipoMovimiento.INGRESO));
        lblGastos.setText(formatearMonto(service.totalGastos(lote), TipoMovimiento.GASTO));
        long balance = service.balance(lote);
        lblBalance.setText(formatearMonto(Math.abs(balance), balance >= 0 ? TipoMovimiento.INGRESO : TipoMovimiento.GASTO));
        lblBalance.setTextFill(balance >= 0 ? Color.GREEN : Color.RED);
    }

    private String formatearMonto(Long monto, TipoMovimiento tipo) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "CO"));
        nf.setMaximumFractionDigits(0);
        String numero = nf.format(monto).replace(",", ".");
        return (tipo == TipoMovimiento.GASTO ? "-" : "") + "$" + numero;
    }

    @FXML
    private void guardarMovimiento() {
        try {
            String nombre = txtNombre.getText();
            TipoMovimiento tipo = cmbTipo.getValue();
            String montoTexto = txtMonto.getText().trim();
            LocalDate fecha = dpFecha.getValue();
            if (montoTexto.isEmpty()) throw new IllegalArgumentException("El monto no puede estar vacío.");
            Long monto = Long.parseLong(montoTexto);
            LocalDateTime fechaHora = fecha != null
                    ? LocalDateTime.of(fecha, LocalTime.now())
                    : LocalDateTime.now();
            service.guardar(nombre, tipo, monto, fechaHora, lote);
            limpiarFormulario();
            cargarMovimientos();
        } catch (NumberFormatException e) {
            mostrarError("El monto debe ser un número entero válido.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarMovimiento() {
        Movimiento seleccionado = tablaMovimientos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un movimiento para eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar este movimiento?");
        confirmacion.setContentText(seleccionado.getNombre() + " — " +
                formatearMonto(seleccionado.getMonto(), seleccionado.getTipo()));
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            service.eliminar(seleccionado.getId());
            cargarMovimientos();
        }
    }

    @FXML
    private void volverCategoria() {
        Lote loteActual = this.lote;
        MainApp.navegarA("/fxml/categoria-view.fxml", controller -> {
            ((CategoriaController) controller).setCategoria(loteActual.getCategoria());
        });
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        cmbTipo.setValue(null);
        txtMonto.clear();
        dpFecha.setValue(null);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}