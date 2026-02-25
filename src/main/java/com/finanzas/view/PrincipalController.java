package com.finanzas.view;

import com.finanzas.MainApp;
import com.finanzas.model.Categoria;
import com.finanzas.service.CategoriaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PrincipalController {

    @FXML private ListView<Categoria> listaCategorias;
    @FXML private TextField txtNombreCategoria;

    private final CategoriaService categoriaService;
    private final ApplicationContext context;

    public PrincipalController(CategoriaService categoriaService, ApplicationContext context) {
        this.categoriaService = categoriaService;
        this.context = context;
    }

    @FXML
    public void initialize() {
        listaCategorias.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cargarCategorias();
    }

    private void cargarCategorias() {
        listaCategorias.setItems(FXCollections.observableArrayList(categoriaService.listarTodas()));
    }

    @FXML
    private void agregarCategoria() {
        try {
            String nombre = txtNombreCategoria.getText();
            categoriaService.guardar(nombre);
            txtNombreCategoria.clear();
            cargarCategorias();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarCategoria() {
        Categoria seleccionada = listaCategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Selecciona una categoría para eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar la categoría \"" + seleccionada.getNombre() + "\"?");
        confirmacion.setContentText("Se eliminarán todos los lotes y movimientos asociados.");
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            categoriaService.eliminar(seleccionada.getId());
            cargarCategorias();
        }
    }

    @FXML
    private void abrirCategoria() {
        Categoria seleccionada = listaCategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Selecciona una categoría para abrir.");
            return;
        }
        MainApp.navegarA("/fxml/categoria-view.fxml", controller -> {
            ((CategoriaController) controller).setCategoria(seleccionada);
        });
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}