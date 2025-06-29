import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

public class Notepad extends JFrame {
    private JTextPane textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, formatMenu, helpMenu;
    private JMenuItem newMenuItem, newWindowMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem,
            pageSetupMenuItem, printMenuItem, exitMenuItem, undoMenuItem, redoMenuItem,
            cutMenuItem, copyMenuItem, pasteMenuItem, deleteMenuItem, selectAllMenuItem,
            insertDateTimeMenuItem, findMenuItem, findNextMenuItem, findPreviousMenuItem,
            replaceMenuItem, goToMenuItem, toggleWrapMenuItem, changeFontMenuItem,
            searchBingMenuItem, toggleStatusBarMenuItem, helpMenuItem, feedbackMenuItem, aboutMenuItem;

    private JMenuItem undoPopupMentItem, redoPopupMenuItem, cutPopupMenuItem, copyPopupMenuItem,
            pastePopupMenuItem, deletePopupMenuItem, selectAllPopupMenuItem, rightToLeftPopupMenuItem,
            searchBingPopupMenuItem;

    private JPopupMenu popupMenu;

    private JLabel statusBar;

    private JLabel caretPositionLabel;

    private JPanel statusBarContainer;
    private String currentFile;
    private boolean isModified;
    private UndoManager undoManager;
    private String searchText = "";
    private boolean wrapText = false;
    private String lastFindText = "";

    private final String iconBase64 = "AAABAAMAICAQAAAAAADoAgAANgAAADAwAAAAAAAAqA4AAB4DAAAQEBAAAAAAACgBAADGEQAAKAAAACAAAABAAAAAAQAEAAAAAACAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAIAAAACAgACAAAAAgACAAICAAADAwMAAgICAAAAA/wAA/wAAAP//AP8AAAD/AP8A//8AAP///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/////////////wAAAAAAD/////////////8AAAAAAA//////////////AAAAAAAP/////////////wAAAAAAD//wAAAAAAAAAAAAAAAAAA//8O7u7u7u7u7u7u7gAAAP//Du7u7u7u7u7u7u4AAAD/8O7u7u7u7u7u7u7gAAAA//Du7u7u7u7u7u7u4AAAAP/w7u7u7u7u7u7u7uAAAAD/8O7u7u7u7u7u7u7gAAAA/w7u7u7u7u7u7u7uAAAAAP8O7u7u7u7u7u7u7gAAAAD/Du7u7u7u7u7u7u4AAAAA/w7u7u7u7u7u7u7uAAAAAPDu7u7u7u7u7u7u4AAAAADw7u7u7u7u7u7u7uAAAAAA8O7u7u7u7u7u7u7gAAAAAPDu7u7u7u7u7u7u4AAAAAAO7u7u7u7u7u7u7gAAAAAADu7u7u7u7u7u7u4AAAAAAA7u7u7u7u7u7u7uAAAAAAAO7u7u7u7u7u7u4AAAAAAA7u7u7u7u7u7u7uAAAAAAAO7u7u7u7u7u7u7gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//////////4AAAP+AAAD/gAAA/4AAAP+AAAD/gAAAB4AAAAeAAAAHgAAAD4AAAA+AAAAPgAAAD4AAAB+AAAAfgAAAH4AAAB+AAAA/gAAAP4AAAD+AAAA/gAAAf4AAAH+AAAB/gAAAf4AAAP+AAAD/gAAA/////////////////KAAAADAAAABgAAAAAQAIAAAAAACACgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAIAAAACAgACAAAAAgACAAICAAADAwMAAwNzAAPDKpgAEBAQACAgIAAwMDAAREREAFhYWABwcHAAiIiIAKSkpAFVVVQBNTU0AQkJCADk5OQCAfP8AUFD/AJMA1gD/7MwAxtbvANbn5wCQqa0AAAAzAAAAZgAAAJkAAADMAAAzAAAAMzMAADNmAAAzmQAAM8wAADP/AABmAAAAZjMAAGZmAABmmQAAZswAAGb/AACZAAAAmTMAAJlmAACZmQAAmcwAAJn/AADMAAAAzDMAAMxmAADMmQAAzMwAAMz/AAD/ZgAA/5kAAP/MADMAAAAzADMAMwBmADMAmQAzAMwAMwD/ADMzAAAzMzMAMzNmADMzmQAzM8wAMzP/ADNmAAAzZjMAM2ZmADNmmQAzZswAM2b/ADOZAAAzmTMAM5lmADOZmQAzmcwAM5n/ADPMAAAzzDMAM8xmADPMmQAzzMwAM8z/ADP/MwAz/2YAM/+ZADP/zAAz//8AZgAAAGYAMwBmAGYAZgCZAGYAzABmAP8AZjMAAGYzMwBmM2YAZjOZAGYzzABmM/8AZmYAAGZmMwBmZmYAZmaZAGZmzABmmQAAZpkzAGaZZgBmmZkAZpnMAGaZ/wBmzAAAZswzAGbMmQBmzMwAZsz/AGb/AABm/zMAZv+ZAGb/zADMAP8A/wDMAJmZAACZM5kAmQCZAJkAzACZAAAAmTMzAJkAZgCZM8wAmQD/AJlmAACZZjMAmTNmAJlmmQCZZswAmTP/AJmZMwCZmWYAmZmZAJmZzACZmf8AmcwAAJnMMwBmzGYAmcyZAJnMzACZzP8Amf8AAJn/MwCZzGYAmf+ZAJn/zACZ//8AzAAAAJkAMwDMAGYAzACZAMwAzACZMwAAzDMzAMwzZgDMM5kAzDPMAMwz/wDMZgAAzGYzAJlmZgDMZpkAzGbMAJlm/wDMmQAAzJkzAMyZZgDMmZkAzJnMAMyZ/wDMzAAAzMwzAMzMZgDMzJkAzMzMAMzM/wDM/wAAzP8zAJn/ZgDM/5kAzP/MAMz//wDMADMA/wBmAP8AmQDMMwAA/zMzAP8zZgD/M5kA/zPMAP8z/wD/ZgAA/2YzAMxmZgD/ZpkA/2bMAMxm/wD/mQAA/5kzAP+ZZgD/mZkA/5nMAP+Z/wD/zAAA/8wzAP/MZgD/zJkA/8zMAP/M/wD//zMAzP9mAP//mQD//8wAZmb/AGb/ZgBm//8A/2ZmAP9m/wD//2YAIQClAF9fXwB3d3cAhoaGAJaWlgDLy8sAsrKyANfX1wDd3d0A4+PjAOrq6gDx8fEA+Pj4APD7/wCkoKAAgICAAAAA/wAA/wAAAP//AP8AAAD/AP8A//8AAP///wAKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgr//////////////////////////////////////////woKCgoKCgoKCgoKCgoKCgr//////////////////////////////////////////woKCgoKCgoKCgoKCgoKCgr//////////////////////////////////////////woKCgoKCgoKCgoKCgoKCgr//////////////////////////////////////////woKCgoKCgoKCgoKCgoKCgr//////////////////////////////////////////woKCgoKCgoKCgoKCgoKCgr//////////////////////////////////////////woKCgoKCgoKCgoKCgoKCgr/////////CgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgr/////////Cv7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgr///////8K/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgr///////8K/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgr///////8K/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgr///////8K/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgr//////wr+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgr//////wr+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgr//////wr+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v4KCgoKCgoKCgoKCgr//////wr+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v4KCgoKCgoKCgoKCgr/////Cv7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v4KCgoKCgoKCgoKCgr/////Cv7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v4KCgoKCgoKCgoKCgr/////Cv7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v4KCgoKCgoKCgoKCgr/////Cv7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgoKCgr///8K/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgoKCgr///8K/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgoKCgr///8K/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgoKCgr//wr+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgoKCgr//wr+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgoKCgr//wr+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgoKCgr//wr+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgoKCgr/Cv7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgoKCgr/Cv7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+CgoKCgoKCgoKCgoKCgr/Cv7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v4KCgoKCgoKCgoKCgoKCgr/Cv7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v4KCgoKCgoKCgoKCgoKCgoK/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v4KCgoKCgoKCgoKCgoKCgoK/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v4KCgoKCgoKCgoKCgoKCgoK/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgoKCgoKCgoK/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgoKCgoKCgr+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/goKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgr///////8AAP///////wAA////////AAD///////8AAP///////wAA////////AAD4AAAAAf8AAPgAAAAB/wAA+AAAAAH/AAD4AAAAAf8AAPgAAAAB/wAA+AAAAAH/AAD4AAAAAf8AAPgAAAAABwAA+AAAAAAHAAD4AAAAAAcAAPgAAAAADwAA+AAAAAAPAAD4AAAAAA8AAPgAAAAADwAA+AAAAAAPAAD4AAAAAB8AAPgAAAAAHwAA+AAAAAAfAAD4AAAAAB8AAPgAAAAAHwAA+AAAAAA/AAD4AAAAAD8AAPgAAAAAPwAA+AAAAAA/AAD4AAAAAD8AAPgAAAAAfwAA+AAAAAB/AAD4AAAAAH8AAPgAAAAAfwAA+AAAAAB/AAD4AAAAAP8AAPgAAAAA/wAA+AAAAAD/AAD4AAAAAP8AAPgAAAAA/wAA+AAAAAH/AAD4AAAAAf8AAPgAAAAB/wAA////////AAD///////8AAP///////wAA////////AAAoAAAAEAAAACAAAAABAAQAAAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAgAAAAICAAIAAAACAAIAAgIAAAMDAwACAgIAAAAD/AAD/AAAA//8A/wAAAP8A/wD//wAA////AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/////wAAAA/wAAAAAAAAD/Du7u7uAAAPDu7u7u4AAA8O7u7u4AAADw7u7u7gAAAPDu7u7uAAAADu7u7uAAAAAO7u7u4AAAAA7u7u7gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA//8AAP//AADABwAAwAcAAMABAADAAQAAwAEAAMADAADAAwAAwAMAAMADAADABwAAwAcAAMAHAAD//wAA//8AAA==";

    // 搜索对话框的参数记忆功能
    private boolean isSearchCaseSensitive = false;
    private boolean isSearchLooping = false;
    private boolean isSearchDirectionDown = true;

    private ReplaceDialog replaceDialog;

    private FindDialog findDialog;

    private Font currentFont = new Font("微软雅黑", Font.PLAIN, 16);
    private PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();

    private final ActionListener unimplementedListener = e -> {
        JOptionPane.showMessageDialog(this, "敬请期待", "记事本", JOptionPane.INFORMATION_MESSAGE);
        try {
            throw new NotImplementedException("方法未实现");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    };

    public Notepad() {
        setTitle("无标题 - 记事本");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        byte[] iconBytes = Base64.getDecoder().decode(iconBase64);
        setIconImage(new ImageIcon(iconBytes).getImage());

        // 初始化文本区域
        textArea = new JTextPane();
        textArea.setFont(currentFont);
        textArea.getDocument().addDocumentListener(new TextAreaListener());
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // 初始化菜单栏
        menuBar = new JMenuBar();
        fileMenu = new JMenu("文件(F)");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu = new JMenu("编辑(E)");
        editMenu.setMnemonic(KeyEvent.VK_E);
        formatMenu = new JMenu("格式(O)");
        formatMenu.setMnemonic(KeyEvent.VK_O);
        helpMenu = new JMenu("帮助(H)");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        newMenuItem = new JMenuItem("新建(N)");
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        newMenuItem.addActionListener(e -> newFile());
        fileMenu.add(newMenuItem);

        newWindowMenuItem = new JMenuItem("新窗口(W)");
        newWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        newWindowMenuItem.setMnemonic(KeyEvent.VK_W);
        newWindowMenuItem.addActionListener(e -> newWindow());
        fileMenu.add(newWindowMenuItem);

        openMenuItem = new JMenuItem("打开(O)...");
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.addActionListener(e -> openFile());
        fileMenu.add(openMenuItem);

        saveMenuItem = new JMenuItem("保存(S)");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.addActionListener(e -> saveFile());
        fileMenu.add(saveMenuItem);

        saveAsMenuItem = new JMenuItem("另存为(A)...");
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
        saveAsMenuItem.addActionListener(e -> saveAsFile(true));
        fileMenu.add(saveAsMenuItem);

        fileMenu.addSeparator();

        pageSetupMenuItem = new JMenuItem("页面设置(U)...");
        pageSetupMenuItem.setMnemonic(KeyEvent.VK_U);
        pageSetupMenuItem.addActionListener(e -> pageSetup());
        fileMenu.add(pageSetupMenuItem);

        printMenuItem = new JMenuItem("打印(P)...");
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        printMenuItem.setMnemonic(KeyEvent.VK_P);
        printMenuItem.addActionListener(e -> printFile());
        fileMenu.add(printMenuItem);

        fileMenu.addSeparator();

        exitMenuItem = new JMenuItem("退出(X)");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.addActionListener(e -> exit());
        fileMenu.add(exitMenuItem);

        undoMenuItem = new JMenuItem("撤销(U)");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoMenuItem.setMnemonic(KeyEvent.VK_U);
        undoMenuItem.addActionListener(e -> undo());
        undoMenuItem.setEnabled(false);
        editMenu.add(undoMenuItem);

        redoMenuItem = new JMenuItem("重做(E)");
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoMenuItem.setMnemonic(KeyEvent.VK_E);
        redoMenuItem.addActionListener(e -> redo());
        redoMenuItem.setEnabled(false);
        editMenu.add(redoMenuItem);

        editMenu.addSeparator();

        cutMenuItem = new JMenuItem("剪切(T)");
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutMenuItem.setMnemonic(KeyEvent.VK_T);
        cutMenuItem.setEnabled(false);
        cutMenuItem.addActionListener(e -> cut());
        editMenu.add(cutMenuItem);

        copyMenuItem = new JMenuItem("复制(C)");
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyMenuItem.setMnemonic(KeyEvent.VK_C);
        copyMenuItem.setEnabled(false);
        copyMenuItem.addActionListener(e -> copy());
        editMenu.add(copyMenuItem);

        pasteMenuItem = new JMenuItem("粘贴(P)");
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteMenuItem.setMnemonic(KeyEvent.VK_P);
        pasteMenuItem.addActionListener(e -> paste());
        editMenu.add(pasteMenuItem);

        deleteMenuItem = new JMenuItem("删除(L)");
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteMenuItem.setMnemonic(KeyEvent.VK_L);
        deleteMenuItem.setEnabled(false);
        deleteMenuItem.addActionListener(e -> delete());
        editMenu.add(deleteMenuItem);

        editMenu.addSeparator();

        searchBingMenuItem = new JMenuItem("使用 Bing 搜索(B)...");
        searchBingMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        searchBingMenuItem.setMnemonic(KeyEvent.VK_B);
        searchBingMenuItem.setEnabled(false);
        searchBingMenuItem.addActionListener(e -> searchBing());
        editMenu.add(searchBingMenuItem);

        findMenuItem = new JMenuItem("查找(F)...");
        findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        findMenuItem.setMnemonic(KeyEvent.VK_F);
        findMenuItem.addActionListener(e -> find());
        editMenu.add(findMenuItem);

        findNextMenuItem = new JMenuItem("查找下一个(N)");
        findNextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        findNextMenuItem.setMnemonic(KeyEvent.VK_N);
        findNextMenuItem.addActionListener(e -> findNext());
        findNextMenuItem.setEnabled(false);
        editMenu.add(findNextMenuItem);

        findPreviousMenuItem = new JMenuItem("查找上一个(V)");
        findPreviousMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK));
        findPreviousMenuItem.setMnemonic(KeyEvent.VK_V);
        findPreviousMenuItem.addActionListener(e -> findPrevious());
        findPreviousMenuItem.setEnabled(false);
        editMenu.add(findPreviousMenuItem);

        replaceMenuItem = new JMenuItem("替换(R)...");
        replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK)); // TODO 原有的快捷键 Ctrl+H 和JTextPane 的退格快捷键冲突，所以用 Ctrl+R 代替。使用 Ctrl+H 需要重写 JTextPane 类
        replaceMenuItem.setMnemonic(KeyEvent.VK_R);
        replaceMenuItem.addActionListener(e -> replace());
        editMenu.add(replaceMenuItem);

        goToMenuItem = new JMenuItem("转到(G)...");
        goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        goToMenuItem.setMnemonic(KeyEvent.VK_G);
        goToMenuItem.addActionListener(e -> goTo());
        editMenu.add(goToMenuItem);

        editMenu.addSeparator();

        selectAllMenuItem = new JMenuItem("全选(A)");
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllMenuItem.setMnemonic(KeyEvent.VK_A);
        selectAllMenuItem.addActionListener(e -> selectAll());
        editMenu.add(selectAllMenuItem);

        insertDateTimeMenuItem = new JMenuItem("时间/日期(D)");
        insertDateTimeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        insertDateTimeMenuItem.setMnemonic(KeyEvent.VK_D);
        insertDateTimeMenuItem.addActionListener(e -> insertDateTime());
        editMenu.add(insertDateTimeMenuItem);

        toggleWrapMenuItem = new JCheckBoxMenuItem("自动换行(W)");
        toggleWrapMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        toggleWrapMenuItem.setMnemonic(KeyEvent.VK_W);
        toggleWrapMenuItem.addActionListener(unimplementedListener);
        formatMenu.add(toggleWrapMenuItem);

        toggleStatusBarMenuItem = new JCheckBoxMenuItem("状态栏(S)");
        toggleStatusBarMenuItem.setMnemonic(KeyEvent.VK_S);
        toggleStatusBarMenuItem.setSelected(true);
        toggleStatusBarMenuItem.addActionListener(e -> toggleStatusBar());
        formatMenu.add(toggleStatusBarMenuItem);

        formatMenu.addSeparator();

        changeFontMenuItem = new JMenuItem("字体(F)...");
        // changeFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0));
        changeFontMenuItem.setMnemonic(KeyEvent.VK_F);
        changeFontMenuItem.addActionListener(e -> changeFont());
        formatMenu.add(changeFontMenuItem);

        helpMenuItem = new JMenuItem("帮助主题(H)");
        helpMenuItem.setMnemonic(KeyEvent.VK_H);
        helpMenuItem.addActionListener(e -> help());
        helpMenu.add(helpMenuItem);

        feedbackMenuItem = new JMenuItem("发送反馈(F)");
        feedbackMenuItem.setMnemonic(KeyEvent.VK_F);
        feedbackMenuItem.addActionListener(e -> feedback());
        helpMenu.add(feedbackMenuItem);

        helpMenu.addSeparator();

        aboutMenuItem = new JMenuItem("关于记事本(A)");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        popupMenu = new JPopupMenu();

        undoPopupMentItem = new JMenuItem("撤销(U)");
        undoPopupMentItem.setMnemonic(KeyEvent.VK_U);
        undoPopupMentItem.setEnabled(false);
        undoPopupMentItem.addActionListener(e -> undo());
        popupMenu.add(undoPopupMentItem);

        redoPopupMenuItem = new JMenuItem("重做(R)");
        redoPopupMenuItem.setMnemonic(KeyEvent.VK_R);
        redoPopupMenuItem.setEnabled(false);
        redoPopupMenuItem.addActionListener(e -> redo());
        popupMenu.add(redoPopupMenuItem);

        popupMenu.addSeparator();

        cutPopupMenuItem = new JMenuItem("剪切(T)");
        cutPopupMenuItem.setMnemonic(KeyEvent.VK_T);
        cutPopupMenuItem.setEnabled(false);
        cutPopupMenuItem.addActionListener(e -> cut());
        popupMenu.add(cutPopupMenuItem);

        copyPopupMenuItem = new JMenuItem("复制(C)");
        copyPopupMenuItem.setMnemonic(KeyEvent.VK_C);
        copyPopupMenuItem.setEnabled(false);
        copyPopupMenuItem.addActionListener(e -> copy());
        popupMenu.add(copyPopupMenuItem);

        pastePopupMenuItem = new JMenuItem("粘贴(P)");
        pastePopupMenuItem.setMnemonic(KeyEvent.VK_P);
        pastePopupMenuItem.addActionListener(e -> paste());
        popupMenu.add(pastePopupMenuItem);

        deletePopupMenuItem = new JMenuItem("删除(D)");
        deletePopupMenuItem.setMnemonic(KeyEvent.VK_D);
        deletePopupMenuItem.setEnabled(false);
        deletePopupMenuItem.addActionListener(e -> delete());
        popupMenu.add(deletePopupMenuItem);

        popupMenu.addSeparator();

        selectAllPopupMenuItem = new JMenuItem("全选(A)");
        selectAllPopupMenuItem.setMnemonic(KeyEvent.VK_A);
        selectAllPopupMenuItem.addActionListener(e -> selectAll());
        popupMenu.add(selectAllPopupMenuItem);

        popupMenu.addSeparator();

        rightToLeftPopupMenuItem = new JCheckBoxMenuItem("从右到左的阅读顺序(R)");
        rightToLeftPopupMenuItem.setMnemonic(KeyEvent.VK_R);
        rightToLeftPopupMenuItem.addActionListener(e -> toggleRightToLeft());
        popupMenu.add(rightToLeftPopupMenuItem);
        
        popupMenu.addSeparator();
        
        searchBingPopupMenuItem = new JMenuItem("使用 Bing 搜索(B)...");
        searchBingPopupMenuItem.setMnemonic(KeyEvent.VK_B);
        searchBingPopupMenuItem.setEnabled(false);
        searchBingPopupMenuItem.addActionListener(e -> searchBing());
        popupMenu.add(searchBingPopupMenuItem);
        
        textArea.setComponentPopupMenu(popupMenu);

        statusBar = new JLabel("");
        statusBar.setPreferredSize(new Dimension(BigDecimal.valueOf(statusBar.getSize().getWidth()).intValue(), 20));
        caretPositionLabel = new JLabel("第 1 行，第 1 列");
        statusBarContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbcStatusBar = new GridBagConstraints();
        gbcStatusBar.gridx = 0; gbcStatusBar.gridy = 0; gbcStatusBar.weightx = 0.6;
        gbcStatusBar.anchor = GridBagConstraints.WEST;
        statusBarContainer.add(statusBar, gbcStatusBar);
        gbcStatusBar.gridx = 1; gbcStatusBar.gridy = 0; gbcStatusBar.weightx = 0.3;
        gbcStatusBar.anchor = GridBagConstraints.WEST;
        statusBarContainer.add(caretPositionLabel, gbcStatusBar);
        gbcStatusBar.gridx = 2; gbcStatusBar.gridy = 0; gbcStatusBar.weightx = 0.1;
        gbcStatusBar.anchor = GridBagConstraints.EAST;
        JLabel resizeMark = new JLabel("\u28E0"); // 使用Unicode转义序列表示
        resizeMark.setForeground(Color.GRAY);
        statusBarContainer.add(resizeMark, gbcStatusBar);
        add(statusBarContainer, BorderLayout.SOUTH);

        textArea.addCaretListener(e -> {
            int dot = e.getDot();
            int mark = e.getMark();

            // 有文本被选中时，将部分菜单项设为可用；没有选中时，将部分菜单项设为不可用
            searchBingMenuItem.setEnabled(dot != mark);
            searchBingPopupMenuItem.setEnabled(dot != mark);
            cutMenuItem.setEnabled(dot != mark);
            cutPopupMenuItem.setEnabled(dot != mark);
            copyMenuItem.setEnabled(dot != mark);
            copyPopupMenuItem.setEnabled(dot != mark);
            deleteMenuItem.setEnabled(dot != mark);
            deletePopupMenuItem.setEnabled(dot != mark);

            int caretPosition = textArea.getCaretPosition();
            int line;
            try {
                Element root = textArea.getDocument().getDefaultRootElement();
                line = root.getElementIndex(caretPosition);
                int column = caretPosition - root.getElement(line).getStartOffset();
                caretPositionLabel.setText("第 " + (line + 1) + " 行，第 " + (column + 1) + " 列");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // 添加拖放支持到文本区域
        textArea.setDropTarget(new DropTarget() {
            @Override
            public synchronized void dragEnter(DropTargetDragEvent dtde) {
                try {
                    // 只接受文件列表
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrag(DnDConstants.ACTION_COPY);
                        java.util.List<?> list = (java.util.List<?>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                        // 如果只拖入了一个文件，输出其绝对路径到控制台
                        if (list.size() == 1 && list.get(0) instanceof File) {
                            dtde.acceptDrag(DnDConstants.ACTION_COPY);
                        } else {
                            // 拖入多个文件或其他情况时不处理
                            dtde.rejectDrag();
                        }
                    } else {
                        // 不支持的数据类型直接拒绝
                        dtde.rejectDrag();
                    }
                } catch (Exception e) {
                    dtde.rejectDrag();
                }
            }

            @Override
            public synchronized void drop(DropTargetDropEvent dtde) {
                try {
                    // 只接受文件列表
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        java.util.List<?> list = (java.util.List<?>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        
                        // 如果只拖入了一个文件，则尝试打开该文件
                        if (list.size() == 1 && list.get(0) instanceof File) {
                            File file = (File) list.get(0);
                            openFileByAbsolutePath(file.getAbsolutePath());
                        } else {
                            // 拖入多个文件或其他情况时不处理
                            dtde.rejectDrop();
                        }
                    } else {
                        // 不支持的数据类型直接拒绝
                        dtde.rejectDrop();
                    }
                } catch (Exception e) {
                    dtde.rejectDrop();
                }
            }
        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                undoMenuItem.setEnabled(undoManager.canUndo());
                undoPopupMentItem.setEnabled(undoManager.canUndo());
                redoMenuItem.setEnabled(undoManager.canRedo());
                redoPopupMenuItem.setEnabled(undoManager.canRedo());
                updateTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                undoMenuItem.setEnabled(undoManager.canUndo());
                undoPopupMentItem.setEnabled(undoManager.canUndo());
                redoMenuItem.setEnabled(undoManager.canRedo());
                redoPopupMenuItem.setEnabled(undoManager.canRedo());
                updateTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                undoMenuItem.setEnabled(undoManager.canUndo());
                undoPopupMentItem.setEnabled(undoManager.canUndo());
                redoMenuItem.setEnabled(undoManager.canRedo());
                redoPopupMenuItem.setEnabled(undoManager.canRedo());
                updateTitle();
            }

            private void updateTitle() {
                if (currentFile != null) {
                    try {
                        String currentText = textArea.getText();
                        String fileText = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(currentFile)));
                        if (!currentText.equals(fileText)) {
                            setTitle("*" + new File(currentFile).getName() + " - 记事本");
                        } else {
                            setTitle(new File(currentFile).getName() + " - 记事本");
                        }
                    } catch (IOException ex) {
                        // 处理异常情况
                    }
                } else {
                    if (!textArea.getText().trim().isEmpty()) {
                        setTitle("*无标题 - 记事本");
                    }
                }
            }
        });

        // 将文本区域添加到主窗口
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // 关闭窗口时弹出保存对话框
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isModified || (currentFile == null && !textArea.getText().isEmpty())) {
                    int result = JOptionPane.showConfirmDialog(Notepad.this, "你想将更改保存到 " + getTitle().substring(1, getTitle().indexOf(" - 记事本")) + " 吗？", "记事本", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        if (saveFile()) { // 保存成功后关闭程序
                            System.exit(0);
                        }
                    } else if (result == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });


        // 设置窗口可见
        setVisible(true);
    }

    /**
     * 删除选中的文本
     */
    private void delete() {
        if (textArea.getSelectedText() != null) {
            textArea.replaceSelection("");
        }
    }

    /**
     * 跳转对话框（输入行号，跳转到指定行）
     */
    private void goTo() {
        String lineNumber = JOptionPane.showInputDialog(this, "请输入行号：", "跳转", JOptionPane.PLAIN_MESSAGE);
        if (lineNumber != null) {
            // 判断是不是数字
            if (!lineNumber.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "请输入数字！", "记事本 - 跳行", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("请输入数字！");
            }
            int lineNumberInt = Integer.parseInt(lineNumber);
            int lineCount = this.getLineCount();

            if (lineNumberInt > 0 && lineNumberInt <= lineCount) {
                int lineStartOffset = this.getLineStartOffset(lineNumberInt - 1);
                textArea.setCaretPosition(lineStartOffset);
            } else {
                JOptionPane.showMessageDialog(this, "行号超过了总行数", "记事本 - 跳行", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("行号超过了总行数！");
            }
        }
    }

    private int getLineStartOffset(int line) {
        // 获取指定行数开头对应的文本的偏移量
        Document doc = textArea.getDocument();
        Element root = doc.getDefaultRootElement();
        if (line < 1 || line > root.getElementCount()) {
            throw new IllegalArgumentException("行号超出范围");
        }
        Element lineElement = root.getElement(line);

        // 获取行的起始位置（偏移量）
        return lineElement.getStartOffset();
    }

    /**
     * 获取文本行数
     * @return
     */
    private int getLineCount() {
        return textArea.getText().split("\n").length;
    }

    // 查找上一个
    private void findPrevious() {
        if (this.searchText != null) {
            String allText = textArea.getText();
            int caretPosition = textArea.getCaretPosition();
            int foundIndex = -1;

            // 向上查找：从光标位置前一个字符开始搜索
            if (caretPosition > 0) {
                String subContent = allText.substring(0, caretPosition - (this.textArea.getSelectedText() == null ? 0 : this.textArea.getSelectedText().length()));
                foundIndex = subContent.lastIndexOf(searchText);

                if (foundIndex == -1 && isSearchLooping) {
                    // 循环查找：从文档末尾开始重新查找
                    statusBar.setText("从底部开始查找下一项");
                    subContent = allText;
                    foundIndex = subContent.lastIndexOf(searchText);
                }
            }

            if (foundIndex != -1) {
                // 找到匹配项，选中它
                textArea.setCaretPosition(foundIndex);
                textArea.select(foundIndex, foundIndex + searchText.length());
            } else {
                JOptionPane.showMessageDialog(this, "找不到 \"" + searchText + "\"。", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // 查找下一个
    private void findNext() {
        //
        if (this.searchText != null) {
            String allText = textArea.getText();
            int caretPosition = textArea.getCaretPosition();
            int foundIndex = allText.indexOf(searchText, caretPosition);
            
            if (foundIndex == -1 && isSearchLooping) {
                // 如果未找到且启用了循环查找，则从文档开头重新开始查找
                statusBar.setText("从顶部开始查找下一项");
                foundIndex = allText.indexOf(searchText, 0);
            }
            
            if (foundIndex != -1) {
                // 找到匹配项，选中它
                textArea.setCaretPosition(foundIndex + searchText.length());
                textArea.select(foundIndex, foundIndex + searchText.length());
            } else {
                JOptionPane.showMessageDialog(this, "找不到 \"" + searchText + "\"。", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // 打开页面设置对话框
    private void pageSetup() {
        PrinterJob job = PrinterJob.getPrinterJob();
        this.pageFormat = job.pageDialog(this.pageFormat);
    }

    // 打开替换对话框
    private void replace() {
        if (replaceDialog != null && replaceDialog.isVisible()) return;
        replaceDialog = new ReplaceDialog(this, this.textArea);
        replaceDialog.setVisible(true);
    }

    // 从查找对话框跳转到替换对话框
    private void replace(String findText) {
        if (replaceDialog != null && replaceDialog.isVisible()) return;
        replaceDialog = new ReplaceDialog(this, this.textArea, findText);
        replaceDialog.setVisible(true);
    }

    // 在当前光标处插入当前日期时间
    private void insertDateTime() {
        // 获取当前光标位置
        int caretPosition = textArea.getCaretPosition();
        // 如果没有选中文本，在当前光标位置插入当前日期时间；否则用当前日期时间替换选中的文本
        if (textArea.getSelectedText() == null) {
            String allText = textArea.getText();
            textArea.setText(allText.substring(0, caretPosition) + new SimpleDateFormat("HH:mm yyyy/M/d").format(new Date()) + allText.substring(caretPosition));
        } else {
            textArea.replaceSelection(new SimpleDateFormat("HH:mm yyyy/M/d").format(new Date()));
        }
    }

    // 新建文件
    private void newFile() {
        if (isModified) {
            int result = JOptionPane.showConfirmDialog(this, "是否保存当前文件？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                saveFile();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        textArea.setText("");
        currentFile = null;
        isModified = false;
        setTitle("无标题 - 记事本");
        statusBar.setText("");
    }

    // 打开一个相同的本程序进程
    private void newWindow() {
        try {
            // 获取本进程程序文件的完整路径及文件名
            String path = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            // 获取本进程的命令行
            String cmd = System.getProperty("sun.java.command");
            ProcessBuilder processBuilder = new ProcessBuilder(path, cmd);
            processBuilder.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "打开文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 打开文件
    private void openFile() {
        if (isModified) {
            int result = JOptionPane.showConfirmDialog(this, "你想将更改保存到 " + getTitle().substring(1, getTitle().indexOf(" - 记事本")) + " 吗？", "记事本", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                if (!saveFile()) return;
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder text = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line).append("\n");
                }

                if (text.toString().startsWith(".LOG\r") || text.toString().startsWith(".LOG\n")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy/M/d");
                    String datetime = sdf.format(new Date());
                    text.append("\r\n").append(datetime).append("\r\n");
                }

                textArea.setText(text.toString());
                currentFile = file.getAbsolutePath();
                setTitle(file.getName() + " - 记事本");
                isModified = false;
                statusBar.setText("");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "打开文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 从给定绝对路径打开文件
    private void openFileByAbsolutePath(String absolutePath) {
        if (isModified) {
            int result = JOptionPane.showConfirmDialog(this, "你想将更改保存到 " + getTitle().substring(1, getTitle().indexOf(" - 记事本")) + " 吗？", "记事本", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                if (!saveFile()) return;
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        File file = new File(absolutePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }

            if (text.toString().startsWith(".LOG\r") || text.toString().startsWith(".LOG\n")) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy/M/d");
                String datetime = sdf.format(new Date());
                text.append("\r\n").append(datetime).append("\r\n");
            }

            textArea.setText(text.toString());
            currentFile = file.getAbsolutePath();
            setTitle(file.getName() + " - 记事本");
            isModified = false;
            statusBar.setText("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "打开文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }

    }

    // 保存文件
    private boolean saveFile() {
        if (currentFile == null) {
            return saveAsFile(false);
        } else {
            try (FileWriter writer = new FileWriter(currentFile)) {
                writer.write(textArea.getText());
                isModified = false;
                setTitle(currentFile.substring(currentFile.lastIndexOf(File.separator) + 1) + " - 记事本");
                statusBar.setText("已保存");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "保存文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        return true;
    }

    /**
     * 另存为文件
     * @param isSaveAs 执行的是保存还是另存为（控制保存文件对话框标题栏的显示）
     * @return 是否保存了（没有关闭对话框或者点击取消）
     */
    private boolean saveAsFile(boolean isSaveAs) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (isSaveAs) fileChooser.setDialogTitle("另存为");
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.exists() && !file.isDirectory()) {
                int confirm = JOptionPane.showConfirmDialog(this, "文件已存在，是否覆盖？", "提示", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // 覆盖文件
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(textArea.getText());
                        currentFile = file.getAbsolutePath();
                        isModified = false;
                        setTitle(currentFile.substring(currentFile.lastIndexOf(File.separator) + 1) + " - 记事本");
                        statusBar.setText("已保存");
                        return true;
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, "保存文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(textArea.getText());
                currentFile = file.getAbsolutePath();
                isModified = false;
                setTitle(currentFile.substring(currentFile.lastIndexOf(File.separator) + 1) + " - 记事本");
                statusBar.setText("已保存");
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "保存文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    // 打印文件
    private void printFile() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(textArea.getPrintable(null, null), this.pageFormat);
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException e) {
            throw new RuntimeException(e);
        }
    }

    // 退出程序
    private void exit() {
        if (isModified) {
            int result = JOptionPane.showConfirmDialog(this, "你想将更改保存到 " + getTitle().substring(1, getTitle().indexOf(" - 记事本")) + " 吗？", "记事本", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                saveFile();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        System.exit(0);
    }

    // 撤销
    private void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    // 重做
    private void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    // 剪切
    private void cut() {
        textArea.cut();
    }

    // 复制
    private void copy() {
        textArea.copy();
    }

    // 粘贴
    private void paste() {
        textArea.paste();
    }

    // 全选
    private void selectAll() {
        textArea.selectAll();
    }

    // 查找
    private void find() {
        // 调用FindDialog类查找文本
        if (findDialog != null && findDialog.isVisible()) return;
        findDialog = new FindDialog(this, this.textArea);
        findDialog.setVisible(true);
    }

    // 从替换对话框跳转到查找对话框
    private void find(String findText) {
        // 调用FindDialog类查找文本
        if (findDialog != null && findDialog.isVisible()) return;
        findDialog = new FindDialog(this, this.textArea, findText);
        findDialog.setVisible(true);
    }

    // 搜索Bing
    private void searchBing() {
        String searchText = textArea.getSelectedText();
        if (searchText != null && !searchText.isEmpty()) {
            try {
                Desktop.getDesktop().browse(new URI("https://www.bing.com/search?q=" + URLEncoder.encode(searchText, "UTF-8")));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "搜索失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要搜索的文本", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 设置字体
    private void changeFont() {
        JFontChooser fontChooser = new JFontChooser(textArea.getFont());
        int result = fontChooser.showDialog(this);
        if (result == JFontChooser.OK_OPTION) {
            Font font = fontChooser.getSelectedFont();
            textArea.setFont(font);
        }
    }

    // 切换自动换行
    // TODO 暂不做 此功能需要重写JTextPane类
    private void toggleWrap() {

    }

    private void toggleStatusBar() {
        statusBarContainer.setVisible(!statusBarContainer.isVisible());
    }

    // 显示帮助信息
    private void help() {
        // JOptionPane.showMessageDialog(this, "这是一个简单的记事本程序。", "帮助", JOptionPane.INFORMATION_MESSAGE);
        try {
            Desktop.getDesktop().browse(new URI("https://cn.bing.com/search?q=%E8%8E%B7%E5%8F%96%E6%9C%89%E5%85%B3+windows+%E4%B8%AD%E7%9A%84%E8%AE%B0%E4%BA%8B%E6%9C%AC%E7%9A%84%E5%B8%AE%E5%8A%A9"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "功能错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 发送反馈
    private void feedback() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.zhihu.com/question/659364217/answer/3540757546"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "功能错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 显示关于信息
    private void showAbout() {
        JOptionPane.showMessageDialog(this, "版本：5.0\n作者：你", "关于记事本", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleRightToLeft() {
        textArea.setComponentOrientation(textArea.getComponentOrientation().isLeftToRight() ?
                ComponentOrientation.RIGHT_TO_LEFT : ComponentOrientation.LEFT_TO_RIGHT);
    }

    // 文本区域监听器
    private class TextAreaListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            isModified = true;
            statusBar.setText("已修改");
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            isModified = true;
            statusBar.setText("已修改");
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            isModified = true;
            statusBar.setText("已修改");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Notepad notepad = new Notepad();
                notepad.setVisible(true);
            }
        });
    }

    public class FindDialog extends JDialog {
        private JTextField findTextField;
        private JCheckBox matchCaseCheckBox, loopCheckBox;
        private JRadioButton upRadioButton, downRadioButton;
        private JButton findNextButton, cancelButton;
        private JTextPane textArea;
        private Notepad parent;
        private final KeyAdapter escapeKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        };

        private final KeyAdapter ctrlRKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {
                    parent.replace(findTextField.getText());
                    dispose();
                }
            }
        };

        public FindDialog(Notepad parent, JTextPane textArea) {
            super(parent, "查找", false);
            this.parent = parent;
            this.textArea = textArea;
            initializeUI();
        }

        public FindDialog(Notepad parent, JTextPane textArea, String findText) {
            super(parent, "查找", false);
            this.parent = parent;
            this.textArea = textArea;
            initializeUI(findText);
        }

        private void initializeUI() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // 查找内容面板
            findTextField = new JTextField(20);
            JPanel findPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel findLabel = new JLabel("查找内容(N):");
            findLabel.setDisplayedMnemonic(KeyEvent.VK_N);
            findLabel.setLabelFor(findTextField);

            // 有文本被选中时，优先设置为被选中的文本；否则设置为上次查找的文本；如果都没有，则留空
            if (this.textArea.getSelectedText() != null) {
                findTextField.setText(parent.textArea.getSelectedText());
            } else if (!Objects.equals(parent.lastFindText, "")) {
                findTextField.setText(parent.lastFindText);
            }

            findPanel.add(findLabel);
            findPanel.add(findTextField);

            // 复选框面板
            JPanel optionPanel = new JPanel(new GridLayout(3, 1));
            
            // 第一行：区分大小写和循环
            JPanel firstRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            matchCaseCheckBox = new JCheckBox("区分大小写(C)");
            matchCaseCheckBox.setMnemonic(KeyEvent.VK_C);
            loopCheckBox = new JCheckBox("循环(L)");
            loopCheckBox.setMnemonic(KeyEvent.VK_L);
            matchCaseCheckBox.setSelected(parent.isSearchCaseSensitive);
            loopCheckBox.setSelected(parent.isSearchLooping);
            firstRowPanel.add(matchCaseCheckBox);
            firstRowPanel.add(loopCheckBox);
            
            // 第二行：方向选择
            JPanel directionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            ButtonGroup directionGroup = new ButtonGroup();
            upRadioButton = new JRadioButton("向上(U)");
            upRadioButton.setMnemonic(KeyEvent.VK_U);
            downRadioButton = new JRadioButton("向下(D)");
            downRadioButton.setMnemonic(KeyEvent.VK_D);
            directionGroup.add(upRadioButton);
            directionGroup.add(downRadioButton);
            directionPanel.add(new JLabel("方向:"));
            directionPanel.add(upRadioButton);
            directionPanel.add(downRadioButton);
            
            // 读取主窗口记忆的查找方向参数
            if (parent.isSearchDirectionDown) {
                downRadioButton.setSelected(true);
            } else {
                upRadioButton.setSelected(true);
            }

            // 组合面板
            optionPanel.add(firstRowPanel);
            optionPanel.add(directionPanel);

            // 按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            Dimension buttonSize = new Dimension(125, 25);
            findNextButton = new JButton("查找下一个(F)");
            findNextButton.setMnemonic(KeyEvent.VK_F);
            findNextButton.setPreferredSize(buttonSize);
            cancelButton = new JButton("取消");
            cancelButton.setPreferredSize(buttonSize);

            findNextButton.addActionListener(e -> findNext());
            cancelButton.addActionListener(e -> dispose());

            // 只有输入了文本时，才允许点击查找下一个
            if (findTextField.getText().isEmpty()) {
                findNextButton.setEnabled(false);
            }
            findTextField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    findNextButton.setEnabled(!findTextField.getText().isEmpty());
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    findNextButton.setEnabled(!findTextField.getText().isEmpty());
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    findNextButton.setEnabled(!findTextField.getText().isEmpty());
                }
            });

            findTextField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && findTextField.getText().length() > 0) {
                        findNext();
                    }
                }
            });

            setResizable(false);
            findTextField.addKeyListener(escapeKeyAdapter);
            matchCaseCheckBox.addKeyListener(escapeKeyAdapter);
            loopCheckBox.addKeyListener(escapeKeyAdapter);
            upRadioButton.addKeyListener(escapeKeyAdapter);
            downRadioButton.addKeyListener(escapeKeyAdapter);
            findNextButton.addKeyListener(escapeKeyAdapter);
            cancelButton.addKeyListener(escapeKeyAdapter);

            findTextField.addKeyListener(ctrlRKeyAdapter);
            matchCaseCheckBox.addKeyListener(ctrlRKeyAdapter);
            loopCheckBox.addKeyListener(ctrlRKeyAdapter);
            upRadioButton.addKeyListener(ctrlRKeyAdapter);
            downRadioButton.addKeyListener(ctrlRKeyAdapter);
            findNextButton.addKeyListener(ctrlRKeyAdapter);
            cancelButton.addKeyListener(ctrlRKeyAdapter);

            JPanel caseSensitiveAndDirectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            caseSensitiveAndDirectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            caseSensitiveAndDirectionPanel.add(matchCaseCheckBox);
            caseSensitiveAndDirectionPanel.add(directionPanel);
            JPanel loopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            loopPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            loopPanel.add(loopCheckBox);

            buttonPanel.add(findNextButton);
            buttonPanel.add(cancelButton);

            // 组合所有面板
            // 组合所有面板
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(3, 3, 3, 3);
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.75; panel.add(findPanel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.25; panel.add(findNextButton, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.75; panel.add(caseSensitiveAndDirectionPanel, gbc);
            gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.75; panel.add(loopPanel, gbc);
            gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.25; panel.add(cancelButton, gbc);
//            panel.add(findPanel, BorderLayout.NORTH);
//            panel.add(optionPanel, BorderLayout.CENTER);
//            panel.add(buttonPanel, BorderLayout.SOUTH);

            add(panel);
            pack();
            setLocationRelativeTo(parent);
        }

        private void initializeUI(String findText) {
            initializeUI();
            findTextField.setText(findText);
        }

        private void findNext() {
            String textToFind = findTextField.getText();
            if (textToFind.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入要查找的文本。", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String content = textArea.getText();
            int caretPosition = textArea.getCaretPosition();

            int foundIndex = -1;
            boolean matchCase = matchCaseCheckBox.isSelected();
            boolean loop = loopCheckBox.isSelected();
            
            // 如果没有选中"向上"，则默认是"向下"
            boolean searchUp = upRadioButton.isSelected(); 

            if (matchCase) {
                if (searchUp) {
                    // 向上查找：从光标位置前一个字符开始搜索
                    if (caretPosition > 0) {
                        String subContent = content.substring(0, caretPosition - (this.textArea.getSelectedText() == null ? 0 : this.textArea.getSelectedText().length()));
                        int lastIndex = subContent.lastIndexOf(textToFind);
                        if (lastIndex != -1) {
                            foundIndex = lastIndex;
                        } else if (loop) {
                            // 循环查找：从文档末尾开始重新查找
                            parent.statusBar.setText("从底部开始查找下一项");
                            subContent = content;
                            lastIndex = subContent.lastIndexOf(textToFind);
                            if (lastIndex != -1 && lastIndex > caretPosition) {
                                foundIndex = lastIndex;
                            }
                        }
                    }
                } else {
                    // 向下查找
                    foundIndex = content.indexOf(textToFind, caretPosition);
                    if (foundIndex == -1 && loop) {
                        // 循环查找：从文档开头开始重新查找
                        parent.statusBar.setText("从顶部开始查找下一项");
                        foundIndex = content.indexOf(textToFind, 0);
                    }
                }
            } else {
                if (searchUp) {
                    // 向上查找（不区分大小写）
                    if (caretPosition > 0) {
                        String lowerTextToFind = textToFind.toLowerCase();
                        String subContent = content.substring(0, caretPosition - (this.textArea.getSelectedText() == null ? 0 : this.textArea.getSelectedText().length())).toLowerCase();
                        int lastIndex = subContent.lastIndexOf(lowerTextToFind);
                        if (lastIndex != -1) {
                            foundIndex = lastIndex;
                        } else if (loop) {
                            // 循环查找：从文档末尾开始重新查找
                            parent.statusBar.setText("从底部开始查找下一项");
                            subContent = content.toLowerCase();
                            lastIndex = subContent.lastIndexOf(lowerTextToFind);
                            if (lastIndex != -1 && lastIndex > caretPosition) {
                                foundIndex = lastIndex;
                            }
                        }
                    }
                } else {
                    // 向下查找（不区分大小写）
                    String subContent = content.toLowerCase();
                    String lowerTextToFind = textToFind.toLowerCase();
                    foundIndex = subContent.indexOf(lowerTextToFind, caretPosition);
                    if (foundIndex == -1 && loop) {
                        // 循环查找：从文档开头开始重新查找
                        parent.statusBar.setText("从顶部开始查找下一项");
                        foundIndex = subContent.indexOf(lowerTextToFind, 0);
                    }
                }
            }

            if (foundIndex != -1) {
                if (searchUp) {
                    // 向上查找时，将光标定位在匹配项的开始处
                    textArea.setCaretPosition(foundIndex);
                    textArea.select(foundIndex, foundIndex + textToFind.length());
                } else {
                    // 向下查找时，将光标定位在匹配项之后
                    textArea.setCaretPosition(foundIndex + textToFind.length());
                    textArea.select(foundIndex, foundIndex + textToFind.length());
                }
                parent.searchText = textToFind;
            } else {
                JOptionPane.showMessageDialog(this, "找不到 \"" + textToFind + "\"。", "提示", JOptionPane.INFORMATION_MESSAGE);
            }

            // 只有执行了搜索时才更新lastFindText、isSearchCaseSensitive、isSearchLooping
            parent.lastFindText = textToFind;
            parent.searchText = textToFind;
            parent.isSearchCaseSensitive = matchCase;
            parent.isSearchLooping = loop;
            parent.isSearchDirectionDown = !searchUp;
            parent.findNextMenuItem.setEnabled(true);
            parent.findPreviousMenuItem.setEnabled(true);
        }
    }

    public class ReplaceDialog extends JDialog {
        private JTextField findTextField, replaceTextField;
        private JCheckBox matchCaseCheckBox, loopCheckBox;
        private JButton findNextButton, replaceButton, replaceAllButton, cancelButton;
        private JTextPane textArea;
        private Notepad parent;
        private final KeyAdapter escapeKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        };

        private final KeyAdapter ctrlFKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                    parent.find(findTextField.getText());
                    dispose();
                }
            }
        };

        public ReplaceDialog(Notepad parent, JTextPane textArea) {
            super(parent, "替换", false);
            this.parent = parent;
            this.textArea = textArea;
            initializeUI();
        }

        public ReplaceDialog(Notepad parent, JTextPane textArea, String findText) {
            super(parent, "替换", false);
            this.parent = parent;
            this.textArea = textArea;
            initializeUI(findText);
        }

        private void initializeUI() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // 查找内容面板
            JPanel findPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel findLabel = new JLabel("查找内容(N)：");
            findTextField = new JTextField(20);
            findLabel.setDisplayedMnemonic(KeyEvent.VK_N);
            findLabel.setLabelFor(findTextField);
            // 有文本被选中时，优先设置为被选中的文本；否则设置为上次查找的文本；如果都没有，则留空
            if (this.textArea.getSelectedText() != null) {
                findTextField.setText(parent.textArea.getSelectedText());
            } else if (!Objects.equals(parent.lastFindText, "")) {
                findTextField.setText(parent.lastFindText);
            }
            findPanel.add(findLabel);
            findPanel.add(findTextField);

            // 替换为面板
            JPanel replacePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel replaceLabel = new JLabel("替换为(P)：　");
            replaceTextField = new JTextField(20);
            replaceLabel.setDisplayedMnemonic(KeyEvent.VK_P);
            replaceLabel.setLabelFor(replaceTextField);
            replacePanel.add(replaceLabel);
            replacePanel.add(replaceTextField);

            // 复选框面板
            JPanel optionPanel = new JPanel(new GridLayout(1, 2));
            matchCaseCheckBox = new JCheckBox("区分大小写(C)");
            matchCaseCheckBox.setMnemonic(KeyEvent.VK_C);
            loopCheckBox = new JCheckBox("循环(L)");
            loopCheckBox.setMnemonic(KeyEvent.VK_L);
            matchCaseCheckBox.setSelected(parent.isSearchCaseSensitive);
            loopCheckBox.setSelected(parent.isSearchLooping);
//            optionPanel.add(matchCaseCheckBox);
//            optionPanel.add(loopCheckBox);

            // 按钮面板
            JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
            findNextButton = new JButton("查找下一个(F)");
            findNextButton.setMnemonic(KeyEvent.VK_F);
            replaceButton = new JButton("替换(R)");
            replaceButton.setMnemonic(KeyEvent.VK_R);
            replaceAllButton = new JButton("全部替换(A)");
            replaceAllButton.setMnemonic(KeyEvent.VK_A);
            cancelButton = new JButton("取消");


            Dimension buttonSize = new Dimension(125, 25);
            findNextButton.setPreferredSize(buttonSize);
            replaceButton.setPreferredSize(buttonSize);
            replaceAllButton.setPreferredSize(buttonSize);
            cancelButton.setPreferredSize(buttonSize);

            findNextButton.addActionListener(e -> findNext());
            replaceButton.addActionListener(e -> replace());
            replaceAllButton.addActionListener(e -> replaceAll());
            cancelButton.addActionListener(e -> dispose());

            // 只有输入了查找内容时才允许点击按钮
            boolean hasFindText = !findTextField.getText().isEmpty();
            findNextButton.setEnabled(hasFindText);
            replaceButton.setEnabled(hasFindText);
            replaceAllButton.setEnabled(hasFindText);

            // 文本变化监听器
            findTextField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateButtonsEnabled();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateButtonsEnabled();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateButtonsEnabled();
                }

                private void updateButtonsEnabled() {
                    boolean enabled = !findTextField.getText().isEmpty();
                    findNextButton.setEnabled(enabled);
                    replaceButton.setEnabled(enabled);
                    replaceAllButton.setEnabled(enabled);
                }
            });

            setResizable(false);
            findTextField.addKeyListener(escapeKeyAdapter);
            replaceTextField.addKeyListener(escapeKeyAdapter);
            matchCaseCheckBox.addKeyListener(escapeKeyAdapter);
            loopCheckBox.addKeyListener(escapeKeyAdapter);
            findNextButton.addKeyListener(escapeKeyAdapter);
            replaceButton.addKeyListener(escapeKeyAdapter);
            replaceAllButton.addKeyListener(escapeKeyAdapter);
            cancelButton.addKeyListener(escapeKeyAdapter);

            findTextField.addKeyListener(ctrlFKeyAdapter);
            replaceTextField.addKeyListener(ctrlFKeyAdapter);
            matchCaseCheckBox.addKeyListener(ctrlFKeyAdapter);
            loopCheckBox.addKeyListener(ctrlFKeyAdapter);
            findNextButton.addKeyListener(ctrlFKeyAdapter);
            replaceButton.addKeyListener(ctrlFKeyAdapter);
            replaceAllButton.addKeyListener(ctrlFKeyAdapter);
            cancelButton.addKeyListener(ctrlFKeyAdapter);

            findTextField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && findTextField.getText().length() > 0) {
                        findNext();
                    }
                }
            });

            replaceTextField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && findTextField.getText().length() > 0) {
                        findNext();
                    }
                }
            });

            // 组合所有面板
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(3, 3, 3, 3);
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.75; panel.add(findPanel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.25; panel.add(findNextButton, gbc);
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.75; panel.add(replacePanel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.25; panel.add(replaceButton, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.75; panel.add(matchCaseCheckBox, gbc);
            gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.25; panel.add(replaceAllButton, gbc);
            gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.75; panel.add(loopCheckBox, gbc);
            gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.25; panel.add(cancelButton, gbc);

            add(panel);
            pack();
            setLocationRelativeTo(parent);
        }

        private void initializeUI(String findText) {
            initializeUI();
            findTextField.setText(findText);
        }

        // 查找下一个
        private void findNext() {
            String textToFind = findTextField.getText();
            if (textToFind.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入要查找的文本。", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String content = textArea.getText();
            int caretPosition = textArea.getCaretPosition();

            int foundIndex = -1;
            boolean matchCase = matchCaseCheckBox.isSelected();
            boolean loop = loopCheckBox.isSelected();

            if (matchCase) {
                // 向下查找
                foundIndex = content.indexOf(textToFind, caretPosition);
                if (foundIndex == -1 && loop) {
                    // 循环查找：从文档开头开始重新查找
                    parent.statusBar.setText("从顶部开始查找下一项");
                    foundIndex = content.indexOf(textToFind, 0);
                }
            } else {
                // 向下查找（不区分大小写）
                String subContent = content.toLowerCase();
                String lowerTextToFind = textToFind.toLowerCase();
                foundIndex = subContent.indexOf(lowerTextToFind, caretPosition);
                if (foundIndex == -1 && loop) {
                    // 循环查找：从文档开头开始重新查找
                    parent.statusBar.setText("从顶部开始查找下一项");
                    foundIndex = subContent.indexOf(lowerTextToFind, 0);
                }
            }

            if (foundIndex != -1) {
                // 将光标定位在匹配项之后
                textArea.setCaretPosition(foundIndex + textToFind.length());
                textArea.select(foundIndex, foundIndex + textToFind.length());
                // 更新主窗口记忆的查找文本和参数
                parent.searchText = textToFind;
                parent.lastFindText = textToFind;
                parent.isSearchCaseSensitive = matchCase;
                parent.isSearchLooping = loop;
                parent.findNextMenuItem.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "找不到 \"" + textToFind + "\"。", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void findNext(boolean alert) {
            String textToFind = findTextField.getText();
            if (textToFind.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入要查找的文本。", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String content = textArea.getText();
            int caretPosition = textArea.getCaretPosition();

            int foundIndex = -1;
            boolean matchCase = matchCaseCheckBox.isSelected();
            boolean loop = loopCheckBox.isSelected();

            if (matchCase) {
                // 向下查找
                foundIndex = content.indexOf(textToFind, caretPosition);
                if (foundIndex == -1 && loop) {
                    // 循环查找：从文档开头开始重新查找
                    parent.statusBar.setText("从顶部开始查找下一项");
                    foundIndex = content.indexOf(textToFind, 0);
                }
            } else {
                // 向下查找（不区分大小写）
                String subContent = content.toLowerCase();
                String lowerTextToFind = textToFind.toLowerCase();
                foundIndex = subContent.indexOf(lowerTextToFind, caretPosition);
                if (foundIndex == -1 && loop) {
                    // 循环查找：从文档开头开始重新查找
                    parent.statusBar.setText("从顶部开始查找下一项");
                    foundIndex = subContent.indexOf(lowerTextToFind, 0);
                }
            }

            if (foundIndex != -1) {
                // 将光标定位在匹配项之后
                textArea.setCaretPosition(foundIndex + textToFind.length());
                textArea.select(foundIndex, foundIndex + textToFind.length());
                // 更新主窗口记忆的查找文本和参数
                parent.searchText = textToFind;
                parent.lastFindText = textToFind;
                parent.isSearchCaseSensitive = matchCase;
                parent.isSearchLooping = loop;
                parent.findNextMenuItem.setEnabled(true);
            } else if (alert) {
                JOptionPane.showMessageDialog(this, "找不到 \"" + textToFind + "\"。", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        // 替换
        private void replace() {
            String textToFind = findTextField.getText();
            if (textToFind.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入要查找的文本。", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            findNext(false);
            String selectedText = textArea.getSelectedText();
            if (selectedText != null && (matchCaseCheckBox.isSelected() && selectedText.equals(textToFind) || !matchCaseCheckBox.isSelected() && selectedText.equalsIgnoreCase(textToFind))) {
                // 如果选中文本与查找内容相同，则替换
                textArea.replaceSelection(replaceTextField.getText());
                // 更新主窗口记忆的查找文本和参数
                parent.lastFindText = textToFind;
                parent.isSearchCaseSensitive = matchCaseCheckBox.isSelected();
                parent.isSearchLooping = loopCheckBox.isSelected();
            } else {
                // 如果没有匹配的选中文本，则直接查找下一个
                findNext();
            }
        }

        // 全部替换
        private void replaceAll() {
            String textToFind = findTextField.getText();
            if (textToFind.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入要查找的文本。", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String content = textArea.getText();
            StringBuilder result = new StringBuilder();
            int currentIndex = 0;
            int count = 0;

            while (currentIndex < content.length()) {
                int foundIndex;
                if (matchCaseCheckBox.isSelected()) {
                    foundIndex = content.indexOf(textToFind, currentIndex);
                } else {
                    foundIndex = content.toLowerCase().indexOf(textToFind.toLowerCase(), currentIndex);
                }

                if (foundIndex == -1) {
                    result.append(content.substring(currentIndex));
                    break;
                }

                result.append(content.substring(currentIndex, foundIndex));
                result.append(replaceTextField.getText());
                currentIndex = foundIndex + textToFind.length();
                count++;
            }

            if (count > 0) {
                textArea.setText(result.toString());
                JOptionPane.showMessageDialog(this, "已替换 " + count + " 处匹配项。", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "找不到 \"" + textToFind + "\"。", "提示", JOptionPane.INFORMATION_MESSAGE);
            }

            // 更新主窗口记忆的查找文本和参数
            parent.lastFindText = textToFind;
            parent.isSearchCaseSensitive = matchCaseCheckBox.isSelected();
            parent.isSearchLooping = loopCheckBox.isSelected();
        }

        // 忽略大小写比较的辅助方法
        private boolean ignoreCase(boolean ignoreCase) {
            return ignoreCase;
        }
    }

    public class JFontChooser extends JComponent
    {
        // class variables
        /**
         * Return value from <code>showDialog()</code>.
         * @see #showDialog
         **/
        public static final int OK_OPTION = 0;
        /**
         * Return value from <code>showDialog()</code>.
         * @see #showDialog
         **/
        public static final int CANCEL_OPTION = 1;
        /**
         * Return value from <code>showDialog()</code>.
         * @see #showDialog
         **/
        public static final int ERROR_OPTION = -1;
        private final Font DEFAULT_SELECTED_FONT = new Font("Serif", Font.PLAIN, 12);
        private final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 10);
        private final int[] FONT_STYLE_CODES =
                {
                        Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD | Font.ITALIC
                };
        private final String[] DEFAULT_FONT_SIZE_STRINGS =
                {
                        "8", "9", "10", "11", "12", "14", "16", "18", "20",
                        "22", "24", "26", "28", "36", "48", "72",
                };

        // instance variables
        protected int dialogResultValue = ERROR_OPTION;



        private String[] fontStyleNames = null;
        private String[] fontFamilyNames = null;
        private String[] fontSizeStrings = null;
        private JTextField fontFamilyTextField = null;
        private JTextField fontStyleTextField = null;
        private JTextField fontSizeTextField = null;
        private JList fontNameList = null;
        private JList fontStyleList = null;
        private JList fontSizeList = null;
        private JPanel fontNamePanel = null;
        private JPanel fontStylePanel = null;
        private JPanel fontSizePanel = null;
        private JPanel samplePanel = null;
        private JTextField sampleText = null;

        /**
         * Constructs a <code>JFontChooser</code> object.
         **/
        public JFontChooser()
        {
            this(new String[]{
                    "8", "9", "10", "11", "12", "14", "16", "18", "20",
                    "22", "24", "26", "28", "36", "48", "72",
            });
        }

        public JFontChooser(Font font) {
            this(new String[]{
                    "8", "9", "10", "11", "12", "14", "16", "18", "20",
                    "22", "24", "26", "28", "36", "48", "72",
            }, font);
        }

        /**
         * Constructs a <code>JFontChooser</code> object using the given font size array.
         * @param fontSizeStrings  the array of font size string.
         **/
        public JFontChooser(String[] fontSizeStrings)
        {
            if (fontSizeStrings == null)
            {
                fontSizeStrings = DEFAULT_FONT_SIZE_STRINGS;
            }
            this.fontSizeStrings = fontSizeStrings;

            JPanel selectPanel = new JPanel();
            selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
            selectPanel.add(getFontFamilyPanel());
            selectPanel.add(getFontStylePanel());
            selectPanel.add(getFontSizePanel());

            JPanel contentsPanel = new JPanel();
            contentsPanel.setLayout(new GridLayout(2, 1));
            contentsPanel.add(selectPanel, BorderLayout.NORTH);
            contentsPanel.add(getSamplePanel(), BorderLayout.CENTER);

            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.add(contentsPanel);
            this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            this.setSelectedFont(DEFAULT_SELECTED_FONT);
        }

        /**
         *
         * @param font the font param given from the textarea of the main window
         */
        public JFontChooser(String[] fontSizeStrings, Font font)
        {

            if (fontSizeStrings == null)
            {
                fontSizeStrings = DEFAULT_FONT_SIZE_STRINGS;
            }
            this.fontSizeStrings = fontSizeStrings;

            JPanel selectPanel = new JPanel();
            selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
            selectPanel.add(getFontFamilyPanel());
            selectPanel.add(getFontStylePanel());
            selectPanel.add(getFontSizePanel());

            JPanel contentsPanel = new JPanel();
            contentsPanel.setLayout(new GridLayout(2, 1));
            contentsPanel.add(selectPanel, BorderLayout.NORTH);
            contentsPanel.add(getSamplePanel(), BorderLayout.CENTER);

            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.add(contentsPanel);
            this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            this.setSelectedFont(font != null ? font : DEFAULT_SELECTED_FONT);
        }

        public JTextField getFontFamilyTextField()
        {
            if (fontFamilyTextField == null)
            {
                fontFamilyTextField = new JTextField();
                fontFamilyTextField.addFocusListener(
                        new TextFieldFocusHandlerForTextSelection(fontFamilyTextField));
                fontFamilyTextField.addKeyListener(
                        new TextFieldKeyHandlerForListSelectionUpDown(getFontFamilyList()));
                fontFamilyTextField.getDocument().addDocumentListener(
                        new ListSearchTextFieldDocumentHandler(getFontFamilyList()));
                fontFamilyTextField.setFont(DEFAULT_FONT);

            }
            return fontFamilyTextField;
        }

        public JTextField getFontStyleTextField()
        {
            if (fontStyleTextField == null)
            {
                fontStyleTextField = new JTextField();
                fontStyleTextField.addFocusListener(
                        new TextFieldFocusHandlerForTextSelection(fontStyleTextField));
                fontStyleTextField.addKeyListener(
                        new TextFieldKeyHandlerForListSelectionUpDown(getFontStyleList()));
                fontStyleTextField.getDocument().addDocumentListener(
                        new ListSearchTextFieldDocumentHandler(getFontStyleList()));
                fontStyleTextField.setFont(DEFAULT_FONT);
            }
            return fontStyleTextField;
        }

        public JTextField getFontSizeTextField()
        {
            if (fontSizeTextField == null)
            {
                fontSizeTextField = new JTextField();
                fontSizeTextField.addFocusListener(
                        new TextFieldFocusHandlerForTextSelection(fontSizeTextField));
                fontSizeTextField.addKeyListener(
                        new TextFieldKeyHandlerForListSelectionUpDown(getFontSizeList()));
                fontSizeTextField.getDocument().addDocumentListener(
                        new ListSearchTextFieldDocumentHandler(getFontSizeList()));
                fontSizeTextField.setFont(DEFAULT_FONT);
            }
            return fontSizeTextField;
        }

        public JList getFontFamilyList()
        {
            if (fontNameList == null)
            {
                fontNameList = new JList(getFontFamilies());
                fontNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                fontNameList.addListSelectionListener(
                        new ListSelectionHandler(getFontFamilyTextField()));
                fontNameList.setSelectedIndex(0);
                fontNameList.setFont(DEFAULT_FONT);
                fontNameList.setFocusable(false);
            }
            return fontNameList;
        }

        public JList getFontStyleList()
        {
            if (fontStyleList == null)
            {
                fontStyleList = new JList(getFontStyleNames());
                fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                fontStyleList.addListSelectionListener(
                        new ListSelectionHandler(getFontStyleTextField()));
                fontStyleList.setSelectedIndex(0);
                fontStyleList.setFont(DEFAULT_FONT);
                fontStyleList.setFocusable(false);
            }
            return fontStyleList;
        }

        public JList getFontSizeList()
        {
            if (fontSizeList == null)
            {
                fontSizeList = new JList(this.fontSizeStrings);
                fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                fontSizeList.addListSelectionListener(
                        new ListSelectionHandler(getFontSizeTextField()));
                fontSizeList.setSelectedIndex(0);
                fontSizeList.setFont(DEFAULT_FONT);
                fontSizeList.setFocusable(false);
            }
            return fontSizeList;
        }

        /**
         * Get the family name of the selected font.
         * @return  the font family of the selected font.
         *
         * @see #setSelectedFontFamily
         **/
        public String getSelectedFontFamily()
        {
            String fontName = (String) getFontFamilyList().getSelectedValue();
            return fontName;
        }

        /**
         * Get the style of the selected font.
         * @return  the style of the selected font.
         *          <code>Font.PLAIN</code>, <code>Font.BOLD</code>,
         *          <code>Font.ITALIC</code>, <code>Font.BOLD|Font.ITALIC</code>
         *
         * @see java.awt.Font#PLAIN
         * @see java.awt.Font#BOLD
         * @see java.awt.Font#ITALIC
         * @see #setSelectedFontStyle
         **/
        public int getSelectedFontStyle()
        {
            int index = getFontStyleList().getSelectedIndex();
            return FONT_STYLE_CODES[index];
        }

        /**
         * Get the size of the selected font.
         * @return  the size of the selected font
         *
         * @see #setSelectedFontSize
         **/
        public int getSelectedFontSize()
        {
            int fontSize = 1;
            String fontSizeString = getFontSizeTextField().getText();
            while (true)
            {
                try
                {
                    fontSize = Integer.parseInt(fontSizeString);
                    break;
                }
                catch (NumberFormatException e)
                {
                    fontSizeString = (String) getFontSizeList().getSelectedValue();
                    getFontSizeTextField().setText(fontSizeString);
                }
            }

            return fontSize;
        }

        /**
         * Get the selected font.
         * @return  the selected font
         *
         * @see #setSelectedFont
         * @see java.awt.Font
         **/
        public Font getSelectedFont()
        {
            Font font = new Font(getSelectedFontFamily(),
                    getSelectedFontStyle(), getSelectedFontSize());
            return font;
        }

        /**
         * Set the family name of the selected font.
         * @param name  the family name of the selected font.
         *
         * @see
         **/
        public void setSelectedFontFamily(String name)
        {
            String[] names = getFontFamilies();
            for (int i = 0; i < names.length; i++)
            {
                if (names[i].toLowerCase().equals(name.toLowerCase()))
                {
                    getFontFamilyList().setSelectedIndex(i);
                    break;
                }
            }
            updateSampleFont();
        }

        /**
         * Set the style of the selected font.
         * @param style  the size of the selected font.
         *               <code>Font.PLAIN</code>, <code>Font.BOLD</code>,
         *               <code>Font.ITALIC</code>, or
         *               <code>Font.BOLD|Font.ITALIC</code>.
         *
         * @see java.awt.Font#PLAIN
         * @see java.awt.Font#BOLD
         * @see java.awt.Font#ITALIC
         * @see #getSelectedFontStyle
         **/
        public void setSelectedFontStyle(int style)
        {
            for (int i = 0; i < FONT_STYLE_CODES.length; i++)
            {
                if (FONT_STYLE_CODES[i] == style)
                {
                    getFontStyleList().setSelectedIndex(i);
                    break;
                }
            }
            updateSampleFont();
        }

        /**
         * Set the size of the selected font.
         * @param size the size of the selected font
         *
         * @see #getSelectedFontSize
         **/
        public void setSelectedFontSize(int size)
        {
            String sizeString = String.valueOf(size);
            for (int i = 0; i < this.fontSizeStrings.length; i++)
            {
                if (this.fontSizeStrings[i].equals(sizeString))
                {
                    getFontSizeList().setSelectedIndex(i);
                    break;
                }
            }
            getFontSizeTextField().setText(sizeString);
            updateSampleFont();
        }

        /**
         * Set the selected font.
         * @param font the selected font
         *
         * @see #getSelectedFont
         * @see java.awt.Font
         **/
        public void setSelectedFont(Font font)
        {
            setSelectedFontFamily(font.getFamily());
            setSelectedFontStyle(font.getStyle());
            setSelectedFontSize(font.getSize());
        }

        public String getVersionString()
        {
            return ("Version");
        }

        /**
         *  Show font selection dialog.
         *  @param parent Dialog's Parent component.
         *  @return OK_OPTION, CANCEL_OPTION or ERROR_OPTION
         *
         *  @see #OK_OPTION
         *  @see #CANCEL_OPTION
         *  @see #ERROR_OPTION
         **/
        public int showDialog(Component parent)
        {
            dialogResultValue = ERROR_OPTION;
            JDialog dialog = createDialog(parent);
            dialog.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    dialogResultValue = CANCEL_OPTION;
                }
            });

            dialog.setVisible(true);
            dialog.dispose();
            dialog = null;

            return dialogResultValue;
        }

        protected class ListSelectionHandler implements ListSelectionListener
        {
            private JTextComponent textComponent;

            ListSelectionHandler(JTextComponent textComponent)
            {
                this.textComponent = textComponent;
            }

            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getValueIsAdjusting() == false)
                {
                    JList list = (JList) e.getSource();
                    String selectedValue = (String) list.getSelectedValue();

                    String oldValue = textComponent.getText();
                    textComponent.setText(selectedValue);
                    if (!oldValue.equalsIgnoreCase(selectedValue))
                    {
                        textComponent.selectAll();
                        textComponent.requestFocus();
                    }

                    updateSampleFont();
                }
            }
        }

        protected class TextFieldFocusHandlerForTextSelection extends FocusAdapter
        {
            private JTextComponent textComponent;

            public TextFieldFocusHandlerForTextSelection(JTextComponent textComponent)
            {
                this.textComponent = textComponent;
            }

            public void focusGained(FocusEvent e)
            {
                textComponent.selectAll();
            }

            public void focusLost(FocusEvent e)
            {
                textComponent.select(0, 0);
                updateSampleFont();
            }
        }

        protected class TextFieldKeyHandlerForListSelectionUpDown extends KeyAdapter
        {
            private JList targetList;

            public TextFieldKeyHandlerForListSelectionUpDown(JList list)
            {
                this.targetList = list;
            }

            public void keyPressed(KeyEvent e)
            {
                int i = targetList.getSelectedIndex();
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_UP:
                        i = targetList.getSelectedIndex() - 1;
                        if (i < 0)
                        {
                            i = 0;
                        }
                        targetList.setSelectedIndex(i);
                        break;
                    case KeyEvent.VK_DOWN:
                        int listSize = targetList.getModel().getSize();
                        i = targetList.getSelectedIndex() + 1;
                        if (i >= listSize)
                        {
                            i = listSize - 1;
                        }
                        targetList.setSelectedIndex(i);
                        break;
                    default:
                        break;
                }
            }
        }

        protected class ListSearchTextFieldDocumentHandler implements DocumentListener
        {
            JList targetList;

            public ListSearchTextFieldDocumentHandler(JList targetList)
            {
                this.targetList = targetList;
            }

            public void insertUpdate(DocumentEvent e)
            {
                update(e);
            }

            public void removeUpdate(DocumentEvent e)
            {
                update(e);
            }

            public void changedUpdate(DocumentEvent e)
            {
                update(e);
            }

            private void update(DocumentEvent event)
            {
                String newValue = "";
                try
                {
                    Document doc = event.getDocument();
                    newValue = doc.getText(0, doc.getLength());
                }
                catch (BadLocationException e)
                {
                    e.printStackTrace();
                }

                if (newValue.length() > 0)
                {
                    int index = targetList.getNextMatch(newValue, 0, Position.Bias.Forward);
                    if (index < 0)
                    {
                        index = 0;
                    }
                    targetList.ensureIndexIsVisible(index);

                    String matchedName = targetList.getModel().getElementAt(index).toString();
                    if (newValue.equalsIgnoreCase(matchedName))
                    {
                        if (index != targetList.getSelectedIndex())
                        {
                            SwingUtilities.invokeLater(new ListSelector(index));
                        }
                    }
                }
            }

            public class ListSelector implements Runnable
            {
                private int index;

                public ListSelector(int index)
                {
                    this.index = index;
                }

                public void run()
                {
                    targetList.setSelectedIndex(this.index);
                }
            }
        }

        protected class DialogOKAction extends AbstractAction
        {
            protected static final String ACTION_NAME = "确定";
            private JDialog dialog;

            protected DialogOKAction(JDialog dialog)
            {
                this.dialog = dialog;
                putValue(Action.DEFAULT, ACTION_NAME);
                putValue(Action.ACTION_COMMAND_KEY, ACTION_NAME);
                putValue(Action.NAME, (ACTION_NAME));
            }

            public void actionPerformed(ActionEvent e)
            {
                dialogResultValue = OK_OPTION;
                dialog.setVisible(false);
            }
        }

        protected class DialogCancelAction extends AbstractAction
        {
            protected static final String ACTION_NAME = "取消";
            private JDialog dialog;

            protected DialogCancelAction(JDialog dialog)
            {
                this.dialog = dialog;
                putValue(Action.DEFAULT, ACTION_NAME);
                putValue(Action.ACTION_COMMAND_KEY, ACTION_NAME);
                putValue(Action.NAME, (ACTION_NAME));
            }

            public void actionPerformed(ActionEvent e)
            {
                dialogResultValue = CANCEL_OPTION;
                dialog.setVisible(false);
            }
        }

        protected JDialog createDialog(Component parent)
        {
            Frame frame = parent instanceof Frame ? (Frame) parent
                    : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
            JDialog dialog = new JDialog(frame, ("字体"), true);

            Action okAction = new DialogOKAction(dialog);
            Action cancelAction = new DialogCancelAction(dialog);

            JButton okButton = new JButton(okAction);
            okButton.setFont(DEFAULT_FONT);
            JButton cancelButton = new JButton(cancelAction);
            cancelButton.setFont(DEFAULT_FONT);

            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new GridLayout(2, 1));
            buttonsPanel.add(okButton);
            buttonsPanel.add(cancelButton);
            buttonsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 10));

            ActionMap actionMap = buttonsPanel.getActionMap();
            actionMap.put(cancelAction.getValue(Action.DEFAULT), cancelAction);
            actionMap.put(okAction.getValue(Action.DEFAULT), okAction);
            InputMap inputMap = buttonsPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), cancelAction.getValue(Action.DEFAULT));
            inputMap.put(KeyStroke.getKeyStroke("ENTER"), okAction.getValue(Action.DEFAULT));

            JPanel dialogEastPanel = new JPanel();
            dialogEastPanel.setLayout(new BorderLayout());
            dialogEastPanel.add(buttonsPanel, BorderLayout.NORTH);

            dialog.getContentPane().add(this, BorderLayout.CENTER);
            dialog.getContentPane().add(dialogEastPanel, BorderLayout.EAST);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            return dialog;
        }

        protected void updateSampleFont()
        {
            Font font = getSelectedFont();
            getSampleTextField().setFont(font);
        }

        protected JPanel getFontFamilyPanel()
        {
            if (fontNamePanel == null)
            {
                fontNamePanel = new JPanel();
                fontNamePanel.setLayout(new BorderLayout());
                fontNamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                fontNamePanel.setPreferredSize(new Dimension(180, 130));

                JScrollPane scrollPane = new JScrollPane(getFontFamilyList());
                scrollPane.getVerticalScrollBar().setFocusable(false);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                JPanel p = new JPanel();
                p.setLayout(new BorderLayout());
                p.add(getFontFamilyTextField(), BorderLayout.NORTH);
                p.add(scrollPane, BorderLayout.CENTER);

                JLabel label = new JLabel(("字体(F)："));
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setHorizontalTextPosition(JLabel.LEFT);
                label.setLabelFor(getFontFamilyTextField());
                label.setDisplayedMnemonic('F');

                fontNamePanel.add(label, BorderLayout.NORTH);
                fontNamePanel.add(p, BorderLayout.CENTER);

            }
            return fontNamePanel;
        }

        protected JPanel getFontStylePanel()
        {
            if (fontStylePanel == null)
            {
                fontStylePanel = new JPanel();
                fontStylePanel.setLayout(new BorderLayout());
                fontStylePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                fontStylePanel.setPreferredSize(new Dimension(140, 130));

                JScrollPane scrollPane = new JScrollPane(getFontStyleList());
                scrollPane.getVerticalScrollBar().setFocusable(false);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                JPanel p = new JPanel();
                p.setLayout(new BorderLayout());
                p.add(getFontStyleTextField(), BorderLayout.NORTH);
                p.add(scrollPane, BorderLayout.CENTER);

                JLabel label = new JLabel(("字形(Y)："));
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setHorizontalTextPosition(JLabel.LEFT);
                label.setLabelFor(getFontStyleTextField());
                label.setDisplayedMnemonic('Y');

                fontStylePanel.add(label, BorderLayout.NORTH);
                fontStylePanel.add(p, BorderLayout.CENTER);
            }
            return fontStylePanel;
        }

        protected JPanel getFontSizePanel()
        {
            if (fontSizePanel == null)
            {
                fontSizePanel = new JPanel();
                fontSizePanel.setLayout(new BorderLayout());
                fontSizePanel.setPreferredSize(new Dimension(70, 130));
                fontSizePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                JScrollPane scrollPane = new JScrollPane(getFontSizeList());
                scrollPane.getVerticalScrollBar().setFocusable(false);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                JPanel p = new JPanel();
                p.setLayout(new BorderLayout());
                p.add(getFontSizeTextField(), BorderLayout.NORTH);
                p.add(scrollPane, BorderLayout.CENTER);

                JLabel label = new JLabel(("大小(S)："));
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setHorizontalTextPosition(JLabel.LEFT);
                label.setLabelFor(getFontSizeTextField());
                label.setDisplayedMnemonic('S');

                fontSizePanel.add(label, BorderLayout.NORTH);
                fontSizePanel.add(p, BorderLayout.CENTER);
            }
            return fontSizePanel;
        }

        protected JPanel getSamplePanel()
        {
            if (samplePanel == null)
            {
                Border titledBorder = BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), ("示例"));
                Border empty = BorderFactory.createEmptyBorder(5, 10, 10, 10);
                Border border = BorderFactory.createCompoundBorder(titledBorder, empty);

                samplePanel = new JPanel();
                samplePanel.setLayout(new BorderLayout());
                samplePanel.setBorder(border);

                samplePanel.add(getSampleTextField(), BorderLayout.CENTER);
            }
            return samplePanel;
        }

        protected JTextField getSampleTextField()
        {
            if (sampleText == null)
            {
                Border lowered = BorderFactory.createLoweredBevelBorder();

                sampleText = new JTextField(("AaBbYyZz"));
                sampleText.setBorder(lowered);
                sampleText.setPreferredSize(new Dimension(300, 100));
            }
            return sampleText;
        }

        protected String[] getFontFamilies()
        {
            if (fontFamilyNames == null)
            {
                GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
                fontFamilyNames = env.getAvailableFontFamilyNames();
            }
            return fontFamilyNames;
        }

        protected String[] getFontStyleNames()
        {
            if (fontStyleNames == null)
            {
                int i = 0;
                fontStyleNames = new String[4];
                fontStyleNames[i++] = ("常规");
                fontStyleNames[i++] = ("粗体");
                fontStyleNames[i++] = ("倾斜");
                fontStyleNames[i++] = ("粗偏斜体");
            }
            return fontStyleNames;
        }
    }

    static class NotImplementedException extends Exception {
        public NotImplementedException(String message)
        {
            super(message);
        }
    }
}
