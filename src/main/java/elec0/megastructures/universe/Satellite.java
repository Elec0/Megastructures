package elec0.megastructures.universe;

public class Satellite extends Planet
{
    // Satellite position is position relative to the planet it's orbiting

    public Satellite() { super(); }
    public Satellite(long seed) { super(seed); }

    @Override
    public void generate()
    {
        // Generate ore distributions/other attributes
    }
}