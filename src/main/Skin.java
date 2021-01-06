package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

/**
 * Data class which collects as many graphics and audio constants as possible in one place.
 * 
 * Version 1.0.1 changes from getResource() to getResourceAsStream() for loading fonts.
 * 
 * @author John McCullock
 * @version 1.0.1 2015-07-25
 */
public class Skin
{
	private static Font MAIN_FONT = null;
	private static Font PLAIN_FONT = null;
	
	static
	{
		try{
			Skin.MAIN_FONT = Font.createFont(Font.TRUETYPE_FONT, Skin.class.getClass().getResourceAsStream("/assets/fonts/Vectorb.ttf"));
			Skin.PLAIN_FONT = Font.createFont(Font.TRUETYPE_FONT, Skin.class.getClass().getResourceAsStream("/assets/fonts/Audiowide-Regular.ttf"));
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public static final Color SCREEN_BACKGROUND = new Color(0, 0, 0, 255);
	public static final String NORMAL_CURSOR_IMAGE = "/assets/cursors/cursor2.png";
	
	public static final Font LARGE_LABEL_FONT = MAIN_FONT;
	public static final Color LARGE_LABEL_FONT_COLOR = new Color(255, 255, 255, 255);
	public static final int LARGE_LABEL_FONT_STYLE = Font.BOLD;
	public static final float LARGE_LABEL_FONT_FACTOR = 0.03125f; // by screen height.
	
	public static final Font SMALL_LABEL_FONT = MAIN_FONT;
	public static final Color SMALL_LABEL_FONT_COLOR = new Color(255, 255, 255, 255);
	public static final int SMALL_LABEL_FONT_STYLE = Font.BOLD;
	public static final float SMALL_LABEL_FONT_FACTOR = 0.01666f; // by screen height.
	
	public static final Font FPS_LABEL_FONT = Skin.PLAIN_FONT;
	public static final Color FPS_LABEL_FOREGROUND = new Color(255, 255, 255, 255);
	public static final int FPS_LABEL_FONT_STYLE = Font.PLAIN;
	public static final float FPS_LABEL_FONT_FACTOR = 0.01904f; // by screen height.
	
	public static final Font GAME_OVER_FONT = Skin.MAIN_FONT;
	public static final Color GAME_OVER_FOREGROUND = new Color(255, 255, 255, 255);
	public static final int GAME_OVER_FONT_STYLE = Font.PLAIN;
	public static final float MEGA_FONT_FACTOR = 0.0666f; // by screen height
	
	public static final Font MENU_FONT = Skin.PLAIN_FONT;
	public static final Color NORMAL_BUTTON_BORDER = new Color(255, 255, 255, 255);
	public static final BasicStroke NORMAL_BUTTON_BORDER_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Color NORMAL_BUTTON_BACKGROUND = new Color(0, 0, 0, 128);
	public static final Color NORMAL_BUTTON_FOREGROUND = new Color(255, 255, 255, 255);
	public static final int NORMAL_FONT_STYLE = Font.PLAIN;
	public static final Color HOVERED_BUTTON_BORDER = new Color(255, 255, 255, 255);
	public static final BasicStroke HOVERED_BUTTON_BORDER_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Color HOVERED_BUTTON_BACKGROUND = new Color(255, 255, 255, 128);
	public static final Color HOVERED_BUTTON_FOREGROUND = new Color(255, 255, 255, 255);
	public static final int HOVERED_FONT_STYLE = Font.BOLD;
	public static final Color SELECTED_BUTTON_BORDER = new Color(255, 255, 255, 255);
	public static final BasicStroke SELECTED_BUTTON_BORDER_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Color SELECTED_BUTTON_BACKGROUND = new Color(255, 255, 255, 255);
	public static final Color SELECTED_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	public static final int SELECTED_FONT_STYLE = Font.BOLD;
	
	public static final Color NORMAL_SETTINGS_VALUE = new Color(128, 128, 128, 255);
	public static final Color CHANGED_SETTINGS_VALUE = new Color(255, 255, 255, 255);
	
	public static final Font OPTIONS_LABEL_FONT = Skin.PLAIN_FONT;
	public static final int OPTIONS_LABEL_FONT_STYLE = Font.PLAIN;
	public static final float OPTIONS_LABEL_FONT_FACTOR = 0.03428f; // by screen height.
	public static final Color OPTIONS_LABEL_FOREGROUND = new Color(255, 255, 255, 255);
	public static final Color TEXT_BOX_BORDER = new Color(255, 255, 255, 255);
	public static final BasicStroke TEXT_BOX_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font TEXT_BOX_FONT = Skin.PLAIN_FONT;
	public static final int TEXT_BOX_FONT_STYLE = Font.BOLD + Font.ITALIC;
	public static final float TEXT_BOX_FONT_FACTOR = 0.0266f; // by screen height.
	public static final Color TEXT_BOX_NORMAL_FOREGROUND = new Color(128, 128, 128, 255);
	public static final Color TEXT_BOX_CHANGED_FOREGROUND = new Color(255, 255, 255, 255);
	
	public static final Color LIST_BACKGROUND = new Color(0, 0, 0, 255);
	public static final Color LIST_BORDER = new Color(255, 255, 255, 255);
	public static final BasicStroke LIST_CONTENT_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Color NORMAL_CELL_BACKGROUND = new Color(0, 0, 0, 255);
	public static final Color NORMAL_CELL_BORDER = new Color(128, 128, 128, 255);
	public static final Color NORMAL_CELL_TEXT = new Color(255, 255, 255, 255);
	public static final BasicStroke NORMAL_CELL_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font NORMAL_CELL_FONT = Skin.PLAIN_FONT;
	public static final int NORMAL_CELL_FONT_STYLE = Font.PLAIN;
	public static final float NORMAL_CELL_FONT_FACTOR = 0.01523f;
	public static final Color HOVERED_CELL_BACKGROUND = new Color(128, 128, 128, 255);
	public static final Color HOVERED_CELL_BORDER = new Color(255, 255, 255, 255);
	public static final BasicStroke HOVERED_CELL_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Color HOVERED_CELL_TEXT = new Color(255, 255, 255, 255);
	public static final Font HOVERED_CELL_FONT = Skin.PLAIN_FONT;
	public static final int HOVERED_CELL_FONT_STYLE = Font.BOLD;
	public static final float HOVERED_CELL_FONT_FACTOR = 0.01523f;
	public static final Color SELECTED_CELL_BACKGROUND = new Color(255, 255, 255, 255);
	public static final Color SELECTED_CELL_BORDER = new Color(128, 128, 128, 255);
	public static final Color SELECTED_CELL_TEXT = new Color(0, 0, 0, 255);
	public static final BasicStroke SELECTED_CELL_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font SELECTED_CELL_FONT = Skin.PLAIN_FONT;
	public static final int SELECTED_CELL_FONT_STYLE = Font.BOLD;
	public static final float SELECTED_CELL_FONT_FACTOR = 0.01523f;
	
	public static final Color NORMAL_SCROLL_TRACK_BACKGROUND = new Color(0, 0, 0, 255);
	public static final Color NORMAL_SCROLL_TRACK_BORDER = new Color(128, 128, 128, 255);
	public static final BasicStroke NORMAL_SCROLL_TRACK_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Color NORMAL_INCREMENT_DECREMENT_BUTTON_BACKGROUND = new Color(255, 255, 255, 255);
	public static final Color NORMAL_SCROLL_GRIP_COLOR = new Color(128, 128, 128, 255);
	public static final Color HOVERED_SCROLL_TRACK_BACKGROUND = new Color(0, 0, 0, 255);
	public static final Color HOVERED_SCROLL_TRACK_BORDER = new Color(192, 192, 192, 255);
	public static final BasicStroke HOVERED_SCROLL_TRACK_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Color HOVERED_INCREMENT_DECREMENT_BACKGROUND = new Color(255, 255, 255, 255);
	public static final Color HOVERED_SCROLL_GRIP_COLOR = new Color(192, 192, 192, 255);
	public static final Color SELECTED_SCROLL_TRACK_BACKGROUND = new Color(0, 0, 0, 255);
	public static final Color SELECTED_SCROLL_TRACK_BORDER = new Color(255, 255, 255, 255);
	public static final BasicStroke SELECTED_SCROLL_TRACK_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Color SELECTED_DECREMENT_INCREMENT_BACKGROUND = new Color(255, 255, 255, 255);
	public static final Color SELECTED_SCROLL_GRIP_COLOR = new Color(255, 255, 255, 255);
	
	public static final Font KEY_BINDING_BUTTON_FONT = Skin.PLAIN_FONT;
	public static final int KEY_BINDING_BUTTON_FONT_STYLE = Font.PLAIN;
	
	public static final Color MESSAGE_WINDOW_BACKGROUND = new Color(192, 192, 192, 255);
	public static final Color MESSAGE_WINDOW_FOREGROUND = new Color(0, 0, 0, 255);
	public static final Color MESSAGE_WINDOW_BORDER_COLOR = new Color (255, 255, 255, 255);
	public static final BasicStroke MESSAGE_WINDOW_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font MESSAGE_WINDOW_FONT = Skin.PLAIN_FONT;
	public static final int MESSAGE_WINDOW_FONT_STYLE = Font.PLAIN;
	public static final float MESSAGE_WINDOW_FONT_FACTOR = 0.0222f;
	public static final Color NORMAL_MESSAGE_WINDOW_BUTTON_BACKGROUND = new Color(128, 128, 128, 255);
	public static final Color NORMAL_MESSAGE_WINDOW_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	public static final Color NORMAL_MESSAGE_WINDOW_BUTTON_BORDER = new Color(0, 0, 0, 255);
	public static final BasicStroke NORMAL_WINDOW_BUTTON_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font NORMAL_MESSAGE_WINDOW_BUTTON_FONT = Skin.PLAIN_FONT;
	public static final int NORMAL_MESSAGE_WINDOW_BUTTON_FONT_STYLE = Font.PLAIN;
	public static final float NORMAL_MESSAGE_WINDOW_BUTTON_FONT_FACTOR = 0.0222f;
	public static final Color HOVERED_MESSAGE_WINDOW_BUTTON_BACKGROUND = new Color(192, 192, 192, 255);
	public static final Color HOVERED_MESSAGE_WINDOW_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	public static final Color HOVERED_MESSAGE_WINDOW_BUTTON_BORDER = new Color(64, 64, 64, 255);
	public static final BasicStroke HOVERED_WINDOW_BUTTON_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font HOVERED_MESSAGE_WINDOW_BUTTON_FONT = Skin.PLAIN_FONT;
	public static final int HOVERED_MESSAGE_WINDOW_BUTTON_FONT_STYLE = Font.BOLD;
	public static final float HOVERED_MESSAGE_WINDOW_BUTTON_FONT_FACTOR = 0.0222f;
	public static final Color SELECTED_MESSAGE_WINDOW_BUTTON_BACKGROUND = new Color(255, 255, 255, 255);
	public static final Color SELECTED_MESSAGE_WINDOW_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	public static final Color SELECTED_MESSAGE_WINDOW_BUTTON_BORDER = new Color(128, 128, 128, 255);
	public static final BasicStroke SELECTED_WINDOW_BUTTON_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font SELECTED_MESSAGE_WINDOW_BUTTON_FONT = Skin.PLAIN_FONT;
	public static final int SELECTED_MESSAGE_WINDOW_BUTTON_FONT_STYLE = Font.BOLD;
	public static final float SELECTED_MESSAGE_WINDOW_BUTTON_FONT_FACTOR = 0.0222f;
	
	public static final Font HIGH_SCORE_WINDOW_FONT = Skin.MAIN_FONT;
	public static final int HIGH_SCORE_WINDOW_FONT_STYLE = Font.PLAIN;
	public static final float HIGH_SCORE_WINDOW_FONT_FACTOR = 0.03125f; // by screen height.
	public static final Color HIGH_SCORE_WINDOW_FOREGROUND = new Color(255, 255, 255, 255);
	public static final Color ENTRY_WINDOW_BACKGROUND = new Color(192, 192, 192, 255);
	public static final Color ENTRY_WINDOW_FOREGROUND = new Color(0, 0, 0, 255);
	public static final Color ENTRY_WINDOW_BORDER_COLOR = new Color(96, 96, 96, 255);
	public static final BasicStroke ENTRY_WINDOW_BORDER_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font ENTRY_WINDOW_FONT = Skin.PLAIN_FONT;
	public static final int ENTRY_WINDOW_FONT_STYLE = Font.PLAIN;
	public static final float ENTRY_WINDOW_FONT_FACTOR = 0.0238f; // by screen height.
	public static final Color ENTRY_TEXTBOX_BACKGROUND = new Color(0, 0, 0, 255);
	public static final Color ENTRY_TEXTBOX_FOREGROUND = new Color(255, 255, 255, 255);
	public static final Color ENTRY_TEXTBOX_BORDER = new Color(64, 64, 64, 255);
	public static final Font ENTRY_TEXTBOX_FONT = Skin.PLAIN_FONT;
	public static final int ENTRY_TEXTBOX_FONT_STYLE = Font.PLAIN;
	public static final float ENTRY_TEXTBOX_FONT_FACTOR = 0.03047f; // by screen height.
	public static final BasicStroke ENTRY_TEXTBOX_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Color NORMAL_ENTRY_WINDOW_BUTTON_BACKGROUND = new Color(128, 128, 128, 255);
	public static final Color NORMAL_ENTRY_WINDOW_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	public static final Color NORMAL_ENTRY_WINDOW_BUTTON_BORDER = new Color(0, 0, 0, 255);
	public static final BasicStroke NORMAL_ENTRY_WINDOW_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font NORMAL_ENTRY_WINDOW_BUTTON_FONT = Skin.PLAIN_FONT;
	public static final int NORMAL_ENTRY_WINDOW_BUTTON_FONT_STYLE = Font.PLAIN;
	public static final float NORMAL_ENTRY_WINDOW_BUTTON_FONT_FACTOR = 0.5f; // by button height.
	public static final Color HOVERED_ENTRY_WINDOW_BUTTON_BACKGROUND = new Color(192, 192, 192, 255);
	public static final Color HOVERED_ENTRY_WINDOW_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	public static final Color HOVERED_ENTRY_WINDOW_BUTTON_BORDER = new Color(64, 64, 64, 255);
	public static final BasicStroke HOVERED_ENTRY_WINDOW_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font HOVERED_ENTRY_WINDOW_BUTTON_FONT = Skin.PLAIN_FONT;
	public static final int HOVERED_ENTRY_WINDOW_BUTTON_FONT_STYLE = Font.BOLD;
	public static final float HOVERED_ENTRY_WINDOW_BUTTON_FONT_FACTOR = 0.5f; // by button height.
	public static final Color SELECTED_ENTRY_WINDOW_BUTTON_BACKGROUND = new Color(255, 255, 255, 255);
	public static final Color SELECTED_ENTRY_WINDOW_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	public static final Color SELECTED_ENTRY_WINDOW_BUTTON_BORDER = new Color(128, 128, 128, 255);
	public static final BasicStroke SELECTED_ENTRY_WINDOW_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static final Font SELECTED_ENTRY_WINDOW_BUTTON_FONT = Skin.PLAIN_FONT;
	public static final int SELECTED_ENTRY_WINDOW_BUTTON_FONT_STYLE = Font.BOLD;
	public static final float SELECTED_ENTRY_WINDOW_BUTTON_FONT_FACTOR = 0.5f; // by button height.
}
