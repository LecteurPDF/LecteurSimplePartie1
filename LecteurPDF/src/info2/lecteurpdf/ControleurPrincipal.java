/*
 * ControleurPrincipal.java                            22/11/2018
 */

package info2.lecteurpdf;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import com.sun.javafx.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Permet de controler les diff�rents objets de SceneBuilder
 * @author kevin.s
 * @version 1.0
 */
public class ControleurPrincipal {

	/** Elements du fichier pdf ouvert en cours ( fichier et page affich�e en ce moment ) */
	private OutilLecture pdf = new OutilLecture();

	private Preferences prefs = Main.prefs;

	@FXML
	private VBox parentVBox;

	/** Permet d'acc�der � la page pr�c�dente */
	@FXML
	private Button btnPrecPage;

	/** Permet d'acc�der � la page suivante */
	@FXML
	private Button btnNextPage;

	/** Permet d'acc�der � la page saisie par l'utilisateur */
	@FXML
	private TextField txbNbPage;

	/** L� o� la page est affich�e */
	@FXML
	private AnchorPane emplacementImage;

	/** La page que l'on affiche sous forme d'ImageView */
	@FXML
	private ImageView affichageImg;

	public void initialize() {

	}

	/**
	 * Prise de la touche clavier utilis�
	 *
	 * @param event
	 */
	@FXML
	void entreeClavier(KeyEvent event) {


		if (event.getCode() == KeyCode.getKeyCode(prefs.get("TOUCHE_PAGE_SUIVANTE", ""))) {
			affichageImg.setImage(pdf.getPrecPage().getImage());
			/* On met l'ImageView � la bonne �chelle */
			resize(affichageImg);
			txbNbPage.setText(Integer.toString(pdf.getPagesCour()));

		} else {
			System.out.println("Pas de preferences");
		}

		// Temporaire
		switch (event.getCode()) {

		case UP:
			affichageImg.setImage(pdf.getNextPage().getImage());
			/* On met l'ImageView � la bonne �chelle */
			resize(affichageImg);
			txbNbPage.setText(Integer.toString(pdf.getPagesCour()));
			break;
		case DOWN:
			affichageImg.setImage(pdf.getPrecPage().getImage());
			/* On met l'ImageView � la bonne �chelle */
			resize(affichageImg);
			txbNbPage.setText(Integer.toString(pdf.getPagesCour()));
			break;
		default:
			break;

		}
	}

	/**
	 * Permet de d�finir le fichier que l'on va afficher
	 * @param event
	 */
	@FXML
	void changerFichier(ActionEvent event) {

		final FileChooser choixFichier = new FileChooser(); // Choisisseur de fichier

		/* Extension obligatoire : .PDF*/
		FileChooser.ExtensionFilter filtreFichierPdf = new FileChooser.ExtensionFilter("Fichier PDF (*.pdf)", "*.pdf");
		choixFichier.getExtensionFilters().add(filtreFichierPdf);

		/* Ouverture de la fenetre pour choix du fichier */
		File file = choixFichier.showOpenDialog(new Stage());

		/* Si le fichier existe, on l'affiche */
		if(file != null) {

			pdf = new OutilLecture(file.getAbsolutePath()); // On cr�e l'objet avec le lien du fichier pdf

			// affichageImg.imageProperty().set(null); TODO : lag sur gros fichiers
			affichageImg.setImage(pdf.getPagePdfToImg(0).getImage()); // On met l'image sur l'�cran

			/* On met l'ImageView � la bonne �chelle */
			resize(affichageImg);

			/* On met au centre */
			// TODO Centrer image

			//emplacementImage.getTopAnchor(affichageImg);

			//System.out.println("Page fini de charg�");
		}
	}



	/**
	 * Permet d'afficher en taille r�elle *1.2 le document
	 * @param toResize L'image que l'on souhaite redimensionner
	 * @return L'ImageView � la bonne taille
	 */
	private ImageView resize(ImageView toResize) {
		toResize.setFitHeight(pdf.getDocument().getPage(pdf.getPagesCour()).getMediaBox().getHeight());
		toResize.setFitWidth(pdf.getDocument().getPage(pdf.getPagesCour()).getMediaBox().getWidth());
		return toResize;
	}


	/**
	 * Permet d'afficher la pr�c�dente page
	 * @param event btnPrecPage
	 */
	@FXML
	void precedentePage(ActionEvent event) {
		affichageImg.setImage(pdf.getPrecPage().getImage());
		/* On met l'ImageView � la bonne �chelle */
		resize(affichageImg);
		txbNbPage.setText(Integer.toString(pdf.getPagesCour()));
	}

	/**
	 * Permet d'afficher la prochaine page
	 * @param event btnNextPage
	 */
	@FXML
	void prochainePage(ActionEvent event) {
		affichageImg.setImage(pdf.getNextPage().getImage());
		/* On met l'ImageView � la bonne �chelle */
		resize(affichageImg);
		txbNbPage.setText(Integer.toString(pdf.getPagesCour()));
	}

	/**
	 * Permet d'afficher la page souhait�e par l'utilisateur
	 * @param event txbNbPage
	 */
	@FXML
	void nbPage(ActionEvent event) {
		affichageImg.setImage(pdf.getPagePdfToImg(Integer.parseInt(txbNbPage.getText())).getImage());
		/* On met l'ImageView � la bonne �chelle */
		resize(affichageImg);
		txbNbPage.setText(Integer.toString(pdf.getPagesCour()));
	}

	@FXML
	void ouvrirPref(ActionEvent event) {
		//TODO: Fenetre preference
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("preference.fxml"));

			Scene scene = new Scene(fxmlLoader.load(), 300, 500);
			Stage stage = new Stage();
			stage.setTitle("Pr�f�rence - Lecteur PDF");
			stage.setScene(scene);

			stage.setResizable(false);

			/* Fenetre modale */
			stage.initOwner( parentVBox.getScene().getWindow() );
			stage.initModality( Modality.APPLICATION_MODAL );
			stage.showAndWait();
		} catch (IOException e) {
			//TODO: Voir classe Logger
			e.printStackTrace();
		}
	}

	/**
	 * Permet de fermer proprement le fichier et la fen�tre
	 * @param event
	 */
	@FXML
	void fermetureFenetre(ActionEvent event) {
		//TODO Gerer la fermeture g�n�ral
		pdf.close();
		Platform.exit();
	}

}
