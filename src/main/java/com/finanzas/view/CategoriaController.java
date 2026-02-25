package com.finanzas.view;

import com.finanzas.MainApp;
import com.finanzas.model.Categoria;
import com.finanzas.model.Lote;
import com.finanzas.service.LoteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class CategoriaController {

    @FXML private Label lblTitulo;
    @FXML private ListView<Lote> listaLotes;
    @FXML private TextField txtNombreLote;
    @FXML private DatePicker dpFechaLote;

    private final LoteService loteService;
    private final ApplicationContext context;
    private Categoria categoria;

    public CategoriaController(LoteService loteService, ApplicationContext context) {
        this.loteService = loteService;
        this.context = context;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        lblTitulo.setText("Categoría: " + categoria.getNombre());
        cargarLotes();
    }

    @FXML
    public void initialize() {
        listaLotes.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Lote item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " — " + item.getFecha());
            }
        });
    }

    private void cargarLotes() {
        listaLotes.setItems(FXCollections.observableArrayList(loteService.listarPorCategoria(categoria)));
    }

    @FXML
    private void agregarLote() {
        try {
            String nombre = txtNombreLote.getText();
            LocalDate fecha = dpFechaLote.getValue();
            if (fecha == null) fecha = LocalDate.now();
            loteService.guardar(nombre, fecha, categoria);
            txtNombreLote.clear();
            dpFechaLote.setValue(null);
            cargarLotes();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarLote() {
        Lote seleccionado = listaLotes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un lote para eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar el lote \"" + seleccionado.getNombre() + "\"?");
        confirmacion.setContentText("Se eliminarán todos los movimientos asociados.");
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            loteService.eliminar(seleccionado.getId());
            cargarLotes();
        }
    }

    @FXML
    private void abrirLote() {
        Lote seleccionado = listaLotes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un lote para abrir.");
            return;
        }
        MainApp.navegarA("/fxml/lote-view.fxml", controller -> {
            ((LoteController) controller).setLote(seleccionado);
        });
    }

    @FXML
    private void volverPrincipal() {
        MainApp.navegarA("/fxml/principal-view.fxml", null);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}