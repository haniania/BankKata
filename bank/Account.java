package bank;

class Account {

    String noun;
    int scales, limite;
    boolean bolt;

    public Account(String noun, int scales, int limite, boolean bolt) {
        this.noun = noun;
        this.scales = scales;
        this.limite = limite;
        this.bolt = bolt;
    }

    public String getNoun() {
        return noun;
    }

    public void setNoun(String noun) {
        this.noun = noun;
    }

    public int getScales() {
        return scales;
    }

    public void setScales(int scales) {
        this.scales = scales;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public boolean isBolt() {
        return bolt;
    }

    public void setBolt(boolean bolt) {
        this.bolt = bolt;
    }


    public String toString() {
        return this.noun + " | " +
                this.scales + " | " +
                this.limite + " | " +
                this.bolt + "\n";
    }
}