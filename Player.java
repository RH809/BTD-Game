public class Player {
    // currency
    // health
    private int health;
    private int money;

    public Player(){
        reset();
    }

    public void subtractHealth(int damage){
        health -= damage;
        if(health <= 0){
            health = 0;
            // game over
        }
    }

    public void addMoney(int add){
        money += add;
    }

    public void spendMoney(int spend){
        money -= spend;
    }

    public int getMoney(){
        return money;
    }

    public int getHealth(){
        return health;
    }

    public void reset(){
        health = 100;
        money = 20000;
    }
}
