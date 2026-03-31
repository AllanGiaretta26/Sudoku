package sudoku.model;

/**
 * Entidade de celula do Sudoku.
 * value=0 representa celula vazia.
 */
public class Cell {
    private int value = 0;
    private boolean fixed = false;

    public Cell(int value, boolean fixed){
        this.value = value;
        this.fixed = fixed;
    }

    public Cell(){}
    
    public int getValue(){
        return this.value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public boolean isFixed(){
        return this.fixed;
    }

    public void setFixed(boolean fixed){
        this.fixed = fixed;
    }

}
