defmodule Lily.Commands do
  use Agent

  def start_link(_init \\ :ok), do:
    Agent.start_link(fn -> %{} end, name: __MODULE__)

  def fetch_command(name), do: Agent.get(__MODULE__, &Map.get(&1, name))

  def add_command(name, function), do: Agent.update(__MODULE__, &Map.put(&1, name, function))

  defmacro command(name, do: body) do
    define = Macro.expand(name, __CALLER__)
    fname = define |> elem(0) |> Atom.to_string

    quote do
      cmd_macro = def unquote(define), do: unquote(body)
      Lily.Commands.add_command(unquote(fname), cmd_macro)
    end
  end
end