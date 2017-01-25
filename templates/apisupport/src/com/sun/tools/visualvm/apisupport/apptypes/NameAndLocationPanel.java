
package com.sun.tools.visualvm.apisupport.apptypes;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.apisupport.project.CreatedModifiedFiles;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.modules.apisupport.project.ui.UIUtil;
import org.netbeans.modules.apisupport.project.ui.wizard.BasicWizardIterator;
import org.netbeans.modules.apisupport.project.universe.ModuleEntry;
import org.netbeans.modules.apisupport.project.universe.NbPlatform;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * The second panel in project template wizard.
 *
 * @author Milos Kleint
 */
final class NameAndLocationPanel extends BasicWizardIterator.Panel {
    
    private static final String PROJECT_TEMPLATES_DIR = "Templates/Project"; // NOI18N
    private static final String DEFAULT_CATEGORY_PATH = PROJECT_TEMPLATES_DIR + "/Other"; // NOI18N
    
    private NewProjectIterator.DataModel data;
    
    /** Creates new NameAndLocationPanel */
    NameAndLocationPanel(WizardDescriptor setting, NewProjectIterator.DataModel data) {
        super(setting);
        this.data = data;
        initComponents();
        initAccessibility();
        Color lblBgr = UIManager.getColor("Label.background"); // NOI18N
        putClientProperty("NewFileWizard_Title", getMessage("LBL_ProjectWizardTitle"));
        modifiedFilesValue.setBackground(lblBgr);
        createdFilesValue.setBackground(lblBgr);
        modifiedFilesValue.setEditable(false);
        createdFilesValue.setEditable(false);
        
        DocumentListener dListener = new UIUtil.DocumentAdapter() {
            public void insertUpdate(DocumentEvent e) {
                if (checkValidity()) {
                    updateData();
                }
            }
        };
        txtName.getDocument().addDocumentListener(dListener);
         if (comCategory.getEditor().getEditorComponent() instanceof JTextField) {
            JTextField txt = (JTextField) comCategory.getEditor().getEditorComponent();
            txt.getDocument().addDocumentListener(dListener);
        }
        if (comPackageName.getEditor().getEditorComponent() instanceof JTextField) {
            JTextField txt = (JTextField)comPackageName.getEditor().getEditorComponent();
            txt.getDocument().addDocumentListener(dListener);
        }
    }
    
    protected void storeToDataModel() {
        updateData();
    }
    
   
    
    private void updateData() {
        data.setPackageName(comPackageName.getEditor().getItem().toString());
        data.setName(txtName.getText().trim());
        data.setCategory(useMainClass.isSelected() ? comCategory.getEditor().getItem().toString() : null);
        NewProjectIterator.generateFileChanges(data);
        CreatedModifiedFiles fls = data.getCreatedModifiedFiles();
        createdFilesValue.setText(generateText(fls.getCreatedPaths()));
        modifiedFilesValue.setText(generateText(fls.getModifiedPaths()));
        //#68294 check if the paths for newly created files are valid or not..
        String[] invalid  = data.getCreatedModifiedFiles().getInvalidPaths();
        if (invalid.length > 0) {
            setError(NbBundle.getMessage(NameAndLocationPanel.class, "ERR_ToBeCreateFileExists", invalid[0]));
        }
        
    }
    
    protected void readFromDataModel() {
        
        checkValidity();
    }
    
    protected String getPanelName() {
        return getMessage("LBL_NameLocation_Title");
    }
    
    private boolean checkValidity() {
//        if (!checkPlatformValidity()) {
//            return false;
//        }
        if (txtName.getText().trim().length() == 0) {
            setError(getMessage("ERR_Name_Prefix_Empty"));
            return false;
        }
        if (!Utilities.isJavaIdentifier(txtName.getText().trim())) {
            setError(getMessage("ERR_Name_Prefix_Invalid"));
            return false;
        }
        String packageName = comPackageName.getEditor().getItem().toString().trim();
        if (packageName.length() == 0 || !UIUtil.isValidPackageName(packageName)) {
            setError(getMessage("ERR_Package_Invalid"));
            return false;
        }

        markValid();
        return true;
    }
    
//    private boolean checkPlatformValidity() {
//        NbModuleProject nbprj = data.getProject().getLookup().lookup(NbModuleProject.class);
//        if (nbprj == null) {
//            //ignore this check for non default netbeans projects.
//            return true;
//        }
//        NbPlatform platform = nbprj.getPlatform(false);
//        if (platform == null) {
//            setError(getMessage("ERR_No_Platform"));
//            return false;
//        }
//        ModuleEntry[] entries = platform.getModules();
//        Collection<String> modules = new HashSet<String>(Arrays.asList(NewProjectIterator.MODULES));
//
//        for (int i = 0; i < entries.length; i++) {
//            modules.remove(entries[i].getCodeNameBase());
//        }
//        if (modules.size() > 0) {
//            setError(getMessage("ERR_Missing_Modules"));
//            return false;
//        }
//        return true;
//    }
//
  
    
    protected HelpCtx getHelp() {
        return new HelpCtx(NameAndLocationPanel.class);
    }
    
    private static String getMessage(String key) {
        return NbBundle.getMessage(NameAndLocationPanel.class, key);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblDisplayName = new javax.swing.JLabel();
        lblProjectName = new javax.swing.JLabel();
        txtProjectName = new JTextField(ProjectUtils.getInformation(this.data.getProject()).getDisplayName());
        lblPackageName = new javax.swing.JLabel();
        comPackageName = UIUtil.createPackageComboBox(this.data.getSourceRootGroup());
        createdFiles = new javax.swing.JLabel();
        modifiedFiles = new javax.swing.JLabel();
        filler = new javax.swing.JLabel();
        createdFilesValue = new javax.swing.JTextArea();
        modifiedFilesValue = new javax.swing.JTextArea();
        comCategory = new javax.swing.JComboBox();
        useMainClass = new javax.swing.JCheckBox();

        lblName.setLabelFor(txtName);
        org.openide.awt.Mnemonics.setLocalizedText(lblName, org.openide.util.NbBundle.getMessage(NameAndLocationPanel.class, "LBL_Name_1_1")); // NOI18N

        lblDisplayName.setLabelFor(comCategory);
        org.openide.awt.Mnemonics.setLocalizedText(lblDisplayName, org.openide.util.NbBundle.getMessage(NameAndLocationPanel.class, "LBL_DisplayName_1_1")); // NOI18N

        lblProjectName.setLabelFor(txtProjectName);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/sun/tools/visualvm/apisupport/apptypes/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(lblProjectName, bundle.getString("LBL_ProjectName_1_1")); // NOI18N

        txtProjectName.setEditable(false);

        lblPackageName.setLabelFor(comPackageName);
        org.openide.awt.Mnemonics.setLocalizedText(lblPackageName, bundle.getString("LBL_PackageName_1_1")); // NOI18N

        comPackageName.setEditable(true);

        createdFiles.setLabelFor(createdFilesValue);
        org.openide.awt.Mnemonics.setLocalizedText(createdFiles, bundle.getString("LBL_CreatedFiles_1_1")); // NOI18N

        modifiedFiles.setLabelFor(modifiedFilesValue);
        org.openide.awt.Mnemonics.setLocalizedText(modifiedFiles, bundle.getString("LBL_ModifiedFiles_1_1")); // NOI18N

        createdFilesValue.setColumns(20);
        createdFilesValue.setRows(5);
        createdFilesValue.setBorder(null);

        modifiedFilesValue.setColumns(20);
        modifiedFilesValue.setRows(5);
        modifiedFilesValue.setToolTipText("modifiedFilesValue");
        modifiedFilesValue.setBorder(null);

        comCategory.setEditable(true);
        comCategory.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "com.toy.anagrams.ui.Anagrams" }));
        comCategory.setNextFocusableComponent(useMainClass);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, useMainClass, org.jdesktop.beansbinding.ELProperty.create("${selected}"), comCategory, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(filler, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(modifiedFiles, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(createdFiles, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblPackageName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblProjectName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblDisplayName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(modifiedFilesValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                    .add(createdFilesValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                    .add(comPackageName, 0, 358, Short.MAX_VALUE)
                    .add(txtProjectName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(comCategory, 0, 332, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(useMainClass))
                    .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(2, 2, 2)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName)
                    .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(lblDisplayName)
                    .add(comCategory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(useMainClass))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProjectName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtProjectName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPackageName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comPackageName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(39, 39, 39)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createdFiles)
                    .add(createdFilesValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(modifiedFiles)
                    .add(modifiedFilesValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filler, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(20, 20, 20))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    
    private void initAccessibility() {
        this.getAccessibleContext().setAccessibleDescription(getMessage("ACS_NameAndLocationPanel"));
        comPackageName.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_PackageName"));
        comCategory.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_DisplayName"));
        txtName.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_Name"));
        txtProjectName.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_ProjectName"));
        createdFilesValue.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_CreatedFilesValue"));
        modifiedFilesValue.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_ModifiedFilesValue"));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comCategory;
    private javax.swing.JComboBox comPackageName;
    private javax.swing.JLabel createdFiles;
    private javax.swing.JTextArea createdFilesValue;
    private javax.swing.JLabel filler;
    private javax.swing.JLabel lblDisplayName;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPackageName;
    private javax.swing.JLabel lblProjectName;
    private javax.swing.JLabel modifiedFiles;
    private javax.swing.JTextArea modifiedFilesValue;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JCheckBox useMainClass;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    
    private static String generateText(String[] relPaths) {
        StringBuffer sb = new StringBuffer();
        if (relPaths.length > 0) {
            for (int i = 0; i < relPaths.length; i++) {
                if (i > 0) {
                    sb.append('\n');
                }
                sb.append(relPaths[i]);
            }
        }
        return sb.toString();
    }
    
}
